package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.ui.EventViewFactory;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class EventControllerModule {
    @Provides
    EventsAdapter provideEventsAdapter(EventViewFactory viewFactory) {
        return new EventsAdapter(viewFactory);
    }

    @Provides
    @Named("birthday_builder")
    Bundle providesBundle() {
        return new Bundle();
    }

    @Provides
    @Named("birthday_builder")
    Intent providesIntent() {
        return new Intent();
    }
}
