package com.lweynant.yearly;

import android.app.Application;

import com.lweynant.yearly.controller.EventControllerModule;
import com.lweynant.yearly.controller.EventsAdapterModule;
import com.lweynant.yearly.model.EventModelModule;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.ui.EventViewModule;
import com.lweynant.yearly.util.DaggerPlatformComponent;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.PlatformModule;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class YearlyApp extends Application implements IStringResources, IEventRepoListener, IComponentRegistry {


    @Inject
    IClock clock;
    @Inject
    EventRepo repo;
    @Inject
    IJsonFileAccessor repoAccessor;
    private BaseYearlyAppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            //in integration tests onCreate will be called multiple times, we will
            // therefor first remove all planted trees (uprootAll()
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

    @Override public BaseYearlyAppComponent getComponent() {
        Timber.d("getComponent");
        if (component == null) {
            Timber.d("creating the production component");
            component = DaggerYearlyAppComponent.builder()
                    .platformComponent(DaggerPlatformComponent.builder().platformModule(new PlatformModule(this)).build())
                    .yearlyAppModule(new YearlyAppModule(this))
                    .eventModelModule(new EventModelModule())
                    .eventViewModule(new EventViewModule())
                    .eventControllerModule(new EventControllerModule())
                    .eventsAdapterModule(new EventsAdapterModule())
                    .build();
            Timber.d("injecting component and registering as listener");
            component.inject(this);
            repo.addListener(this);
        }
        return component;
    }

    public void setComponent(BaseYearlyAppComponent component) {
        Timber.d("setComponent repo: %s fileAccessor: %s", repo == null ? "null" : repo.toString(), repoAccessor == null ? "null" : repoAccessor.toString());
        this.component = component;
        this.component.inject(this);
        Timber.d("injected component repo: %s fileAccessor: %s", repo == null ? "null" : repo.toString(), repoAccessor == null ? "null" : repoAccessor.toString());
    }

    @Override
    public void onDataSetChanged(EventRepo repo) {
        Timber.d("onDataSetChanged");
        Observable<IEvent> events = repo.getEvents();
        Timber.i("archive");
        events.subscribeOn(Schedulers.io())
                .subscribe(new EventRepoSerializerToFileDecorator(repoAccessor, new EventRepoSerializer(clock)));
        Timber.i("set next event");
        LocalDate now = LocalDate.now();
        Observable<NotificationTime> nextAlarmObservable = repo.notificationTimeForFirstUpcomingEvent(now);
        nextAlarmObservable.subscribeOn(Schedulers.io())
                .subscribe(new AlarmGenerator(this));
    }



}
