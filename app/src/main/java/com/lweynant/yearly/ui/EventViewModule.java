package com.lweynant.yearly.ui;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.util.IClock;

import dagger.Module;
import dagger.Provides;

@Module
public class EventViewModule {
    private final IRString rstring;

    public EventViewModule(IRString rstring) {
        this.rstring = rstring;
    }

    @Provides
    EventViewFactory provideEventViewFactory(IClock clock) {
        return new EventViewFactory(rstring, clock);
    }
}
