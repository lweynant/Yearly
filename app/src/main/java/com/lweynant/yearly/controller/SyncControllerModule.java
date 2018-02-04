package com.lweynant.yearly.controller;

import com.lweynant.yearly.DateFormatter;
import com.lweynant.yearly.EventNotification;
import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.controller.list_events.SortedEventsLoader;
import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IPreferences;

import dagger.Module;
import dagger.Provides;
import rx.android.schedulers.AndroidSchedulers;

@Module
public class SyncControllerModule {

    @Provides @PerApp AlarmGenerator providesAlarmGenerator(IAlarm alarm) {
        return new AlarmGenerator(alarm);
    }
    @Provides IEventsLoader providesEventLoader(IEventRepo repo, IClock clock) {
        return new SortedEventsLoader(repo, AndroidSchedulers.mainThread(),  clock);
    }
    @Provides @PerApp IEventNotification provideEventNotification(YearlyApp app, IPreferences preferences,
                                                                  IDateFormatter dateFormatter,
                                                                  IClock clock) {
        return new EventNotification(app.getApplicationContext(), preferences, dateFormatter, clock);
    }

}
