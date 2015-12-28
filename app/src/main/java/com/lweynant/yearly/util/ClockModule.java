package com.lweynant.yearly.util;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ClockModule {

    @Provides @Singleton IClock provideClock(){
        return new Clock();
    }
    @Provides @Singleton IUniqueIdGenerator provideUniqueIdGenerator(){
        return new UUID();
    }
}
