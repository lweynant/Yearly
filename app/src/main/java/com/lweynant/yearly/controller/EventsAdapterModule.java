package com.lweynant.yearly.controller;

import com.lweynant.yearly.ui.EventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class EventsAdapterModule {
    @Provides EventsAdapter provideEventsAdapter(EventViewFactory viewFactory) {
        return new EventsAdapter(viewFactory);
    }
}
