package com.lweynant.yearly.controller;

import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.controller.list_events.EventsLoader;
import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.controller.list_events.ListEventsPresenter;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class SyncControllerModule {

    @Provides @PerApp AlarmGenerator providesAlarmGenerator(IAlarm alarm) {
        return new AlarmGenerator(alarm);
    }
    @Provides IEventsLoader providesEventLoader(EventRepo repo, IClock clock) {
        return new EventsLoader(repo, clock);
    }
}
