package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.EventsAdapter;
import com.lweynant.yearly.ui.EventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class TestEventsAdapterModule {
    @Provides @PerApp CountingIdlingResource providesCountingIdlingResource(){
        return new CountingIdlingResource("eventsAdapterIdlingResource");
    }
    @Provides @PerApp EventsAdapter provideEventsAdapter(CountingIdlingResource idlingResource, EventViewFactory viewFactory) {
        return new SyncWithTestsEventsAdapter(idlingResource, viewFactory);
    }
}
