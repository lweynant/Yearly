package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.EventsAdapter;
import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class TestEventsAdapterModule {
    @Provides @PerApp EventsAdapter provideEventsAdapter(CountingIdlingResource idlingResource, IEventViewFactory viewFactory) {
        return new SyncWithTestsEventsAdapter(idlingResource, viewFactory);
    }
}
