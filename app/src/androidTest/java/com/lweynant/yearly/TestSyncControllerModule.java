package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.list_events.EventsLoader;
import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;

import dagger.Module;
import dagger.Provides;

@Module
public class TestSyncControllerModule {
    @Provides @PerApp
    AlarmGenerator provideAlarmGenerator(CountingIdlingResource idlingResource, IAlarm alarm) {
        return new SyncWithTestsAlarmGenerator(idlingResource, alarm);
    }

    @Provides @PerApp CountingIdlingResource providesCountingIdlingResource(){
        return new CountingIdlingResource("yearly app idling resource");
    }

    @Provides IEventsLoader providesEventLoader(CountingIdlingResource idlingResource,
                                                IEventRepo repo, IClock clock) {
        return new SyncWithTestsEventsLoader(idlingResource, new EventsLoader(repo, clock));
    }
}
