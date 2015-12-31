package com.lweynant.yearly;

import android.app.Application;

import com.lweynant.yearly.controller.EventControllerModule;
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
import com.lweynant.yearly.util.PlatformComponent;
import com.lweynant.yearly.util.PlatformModule;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import dagger.Component;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class YearlyApp extends Application implements IRString, IEventRepoListener {


    @Inject
    IClock clock;
    @Inject
    EventRepo repo;
    @Inject
    IJsonFileAccessor repoAccessor;
    private boolean isInjected = false;
    private BaseYearlyAppComponent component = null;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
        if (component == null) {
            component = DaggerYearlyAppComponent.builder()
                    .platformComponent(DaggerPlatformComponent.builder().platformModule(new PlatformModule(this)).build())
                    .eventModelModule(new EventModelModule())
                    .eventViewModule(new EventViewModule(this))
                    .eventControllerModule(new EventControllerModule())
                    .build();

        }

    }


    @Override
    public String getStringFromId(int id) {
        return getResources().getString(id);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Timber.d("onTerminate");
        repo.removeListener(this);
    }

    public BaseYearlyAppComponent getComponent() {
        Timber.d("getComponent");
        if (!isInjected) {
            //todo this is a test artefact that I should get rid off - is we reach this point it means
            // we are in production code and not in the test - so we need to inject and register as listener
            Timber.d("injecting component and registering as listener");
            component.inject(this);
            repo.addListener(this);
            isInjected = true;
        }
        return component;
    }

    public void setComponent(BaseYearlyAppComponent component) {
        Timber.d("setComponent repo: %s fileAccessor: %s", repo == null ? "null" : repo.toString(), repoAccessor == null ? "null" : repoAccessor.toString());
        this.component = component;
        this.component.inject(this);
        isInjected = true;
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
