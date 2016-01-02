package com.lweynant.yearly.ui;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.util.IClock;

import dagger.Module;
import dagger.Provides;

@Module
public class EventViewModule {

    @Provides EventViewFactory provideEventViewFactory(IStringResources rstring, IClock clock) {
        return new EventViewFactory(rstring, clock);
    }
}
