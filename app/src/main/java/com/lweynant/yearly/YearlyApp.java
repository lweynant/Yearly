package com.lweynant.yearly;

import android.app.Application;

import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.model.ModelModule;
import com.lweynant.yearly.platform.DaggerPlatformComponent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.PlatformModule;
import com.lweynant.yearly.ui.ViewModule;

import net.danlew.android.joda.JodaTimeAndroid;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class YearlyApp extends Application implements IStringResources, IEventRepoListener {


    @Inject IClock clock;
    @Inject IEventRepo repo;
    @Inject IJsonFileAccessor repoAccessor;
    private BaseYearlyAppComponent component;
    @Inject AlarmGenerator alarmGenerator;
    @Inject IStringResources stringResources;
    @Inject NotificationChannels notificationChannels;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            //in integration tests onCreate will be called multiple times, we will
            // therefor first removed all planted trees (uprootAll()
            Timber.uprootAll();
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
        component = null;

    }


    @Override public String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    @Override
    public void onTerminate() {
        Timber.d("onTerminate");
        super.onTerminate();
        repo.removeListener(this);
    }

    public BaseYearlyAppComponent getComponent() {
        Timber.d("getComponent");
        if (component == null) {
            Timber.d("creating the production component");
            YearlyAppComponent cmp = DaggerYearlyAppComponent.builder()
                    .platformComponent(DaggerPlatformComponent.builder().platformModule(new PlatformModule(this)).build())
                    .yearlyAppModule(new YearlyAppModule(this))
                    .modelModule(new ModelModule())
                    .viewModule(new ViewModule())
                    .controllerModule(new ControllerModule(this))
                    .syncControllerModule(new SyncControllerModule())
                    .build();
            Timber.d("injecting component and registering as listener");
            setComponent(cmp);
            notificationChannels.registerNotificationChannels(NotificationChannels.DEFAULT_GROUP_ID,
                    stringResources.getString(R.string.notification_channel_group_default));
        }
        return component;
    }

    public void setComponent(BaseYearlyAppComponent component) {
        Timber.d("setComponent repo: %s fileAccessor: %s", repo == null ? "null" : repo.toString(), repoAccessor == null ? "null" : repoAccessor.toString());
        this.component = component;
        this.component.inject(this);
        repo.addListener(this);
        Timber.d("injected component repo: %s fileAccessor: %s", repo == null ? "null" : repo.toString(), repoAccessor == null ? "null" : repoAccessor.toString());
    }

    @Override
    public void onDataSetChanged(IEventRepo repo) {
        Timber.d("startLoadingData");
        Observable<IEvent> events = repo.getEventsSubscribedOnProperScheduler();
        Timber.i("archive");
        events.subscribe(new EventRepoSerializerToFileDecorator(repoAccessor, new EventRepoSerializer(clock)));
        Timber.i("set next alarm on %s at %d", alarmGenerator, clock.hour());
        alarmGenerator.generate(repo.getEventsSubscribedOnProperScheduler(), clock.now(), clock.hour());
    }


}
