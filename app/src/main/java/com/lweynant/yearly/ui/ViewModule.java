package com.lweynant.yearly.ui;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.platform.IClock;

import dagger.Module;
import dagger.Provides;

@Module
public class ViewModule {

    @Provides IEventViewFactory provideEventViewFactory(IStringResources rstring, IClock clock) {
        return new EventViewFactory(rstring, clock);
    }
    @Provides DateSelector providesDateSelector(IClock clock) {
        return new DateSelector(clock);
    }

}
