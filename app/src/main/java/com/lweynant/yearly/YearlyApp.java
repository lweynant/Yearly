package com.lweynant.yearly;

import android.app.Application;
import android.content.Context;


import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoFileAccessor;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.util.UUID;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class YearlyApp extends Application implements IRString, IEventRepoListener {
    private EventRepo repo;
    private RefWatcher refWatcher;
    private EventRepoFileAccessor repoAccessor;

    public EventRepo getRepo()  {
        if (null == repo){
            Timber.d("repo was not initialized");
            Clock clock = new Clock();
            UUID uuid = new UUID();
            repo = new EventRepo(getRepoAccessor(), clock, uuid);
        }
        Timber.d("getRepo");
        return repo;
    }
    public static RefWatcher getRefWatcher(Context context) {
        YearlyApp application = (YearlyApp) context.getApplicationContext();
        return application.refWatcher;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
        refWatcher= LeakCanary.install(this);
        getRepo().addListener(this);
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

    public EventRepoFileAccessor getRepoAccessor() {
        Timber.d("getRepoAccessor");
        if (repoAccessor == null){
            repoAccessor = new EventRepoFileAccessor(this);
        }
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
