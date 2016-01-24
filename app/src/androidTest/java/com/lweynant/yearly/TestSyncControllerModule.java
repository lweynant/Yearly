package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.EventsAdapter;
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
public class TestSyncControllerModule {
    @Provides @PerApp
    AlarmGenerator provideAlarmGenerator(CountingIdlingResource idlingResource, IAlarm alarm) {
        return new SyncWithTestsAlarmGenerator(idlingResource, alarm);
    }

    @Provides @PerApp CountingIdlingResource providesCountingIdlingResource(){
        return new CountingIdlingResource("yearly app idling resource");
    }

    @Provides IEventsLoader providesEventLoader(CountingIdlingResource idlingResource,
                                                EventRepo repo, IClock clock) {
        return new SyncWithTestsEventsLoader(idlingResource, new EventsLoader(repo, clock));
    }
}
