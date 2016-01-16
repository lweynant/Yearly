package com.lweynant.yearly.controller;

import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class SyncControllerModule {
    @Provides EventsAdapter provideEventsAdapter(IEventViewFactory viewFactory) {
        return new EventsAdapter(viewFactory);
    }
    @Provides @PerApp AlarmGenerator providesAlarmGenerator(IAlarm alarm) {
        return new AlarmGenerator(alarm);
    }

}
