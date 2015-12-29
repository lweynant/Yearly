package com.lweynant.yearly;

import android.app.Application;


import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoFileAccessor;
import com.lweynant.yearly.model.EventRepoModule;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.ClockModule;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;


import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class YearlyApp extends Application implements IRString, IEventRepoListener {
    public void setComponent(YearlyAppComponent component) {
        Timber.d("setComponent repo: %s fileAccessor: %s", repo==null?"null":repo.toString(), repoAccessor==null?"null": repoAccessor.toString());
        component.inject(this);
        Timber.d("injected component repo: %s fileAccessor: %s", repo == null ? "null" : repo.toString(), repoAccessor == null ? "null" : repoAccessor.toString());
    }

    @PerApp
    @Component (modules = {EventRepoModule.class, ClockModule.class})
    public interface ApplicationComponent extends YearlyAppComponent {

    }

    private YearlyAppComponent component = null;

    @Inject EventRepo repo;
    @Inject
    IJsonFileAccessor repoAccessor;

    public EventRepo getRepo()  {
        Timber.d("getRepo");
        if (repo == null){
            component.inject(this);
            repo.addListener(this);
        }
        return repo;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
        if (component == null) {
            component = DaggerYearlyApp_ApplicationComponent.builder()
                    .eventRepoModule(new EventRepoModule(this))
                    .build();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Timber.d("onTerminate");
        getRepo().removeListener(this);
    }

    @Override
    public String getStringFromId(int id)
    {
        return getResources().getString(id);
    }

    public IJsonFileAccessor getRepoAccessor() {
        Timber.d("getRepoAccessor");
        return repoAccessor;
    }

    @Override
    public void onDataSetChanged(EventRepo repo) {
        Timber.d("onDataSetChanged");
        Observable<IEvent> events = repo.getEvents();
        Timber.i("archive");
        events.subscribeOn(Schedulers.io())
                .subscribe(new EventRepoSerializerToFileDecorator(getRepoAccessor(), new EventRepoSerializer(new Clock())));
        Timber.i("set next event");
        LocalDate now = LocalDate.now();
        Observable<NotificationTime> nextAlarmObservable = repo.notificationTimeForFirstUpcomingEvent(now);
        nextAlarmObservable.subscribeOn(Schedulers.io())
                .subscribe(new AlarmGenerator(this));


    }
}
