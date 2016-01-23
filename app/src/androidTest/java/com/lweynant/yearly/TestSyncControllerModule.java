package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.controller.EventsAdapter;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class TestSyncControllerModule {
    @Provides @PerApp EventsAdapter provideEventsAdapter(CountingIdlingResource idlingResource, IEventViewFactory viewFactory) {
        return new SyncWithTestsEventsAdapter(idlingResource, viewFactory);
    }
    @Provides @PerApp
    AlarmGenerator provideAlarmGenerator(CountingIdlingResource idlingResource, IAlarm alarm) {
        return new SyncWithTestsAlarmGenerator(idlingResource, alarm);
    }

    @Provides @PerApp CountingIdlingResource providesCountingIdlingResource(){
        return new CountingIdlingResource("yearly app idling resource");
    }

    @Provides @PerApp
    ListEventsContract.UserActionsListener provedesEventsListPresenter(CountingIdlingResource idlingResource, EventRepoTransaction transaction, IEventNotification eventNotification) {
        return new SyncWithTestsListEventsPresenter(idlingResource, transaction, eventNotification);
    }
}
