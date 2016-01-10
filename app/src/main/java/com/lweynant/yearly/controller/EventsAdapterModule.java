package com.lweynant.yearly.controller;

import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class EventsAdapterModule {
    @Provides EventsAdapter provideEventsAdapter(IEventViewFactory viewFactory) {
        return new EventsAdapter(viewFactory);
    }
}
