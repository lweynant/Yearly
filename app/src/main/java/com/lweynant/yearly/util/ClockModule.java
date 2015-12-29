package com.lweynant.yearly.util;

import com.lweynant.yearly.PerApp;


import dagger.Module;
import dagger.Provides;

@Module
public class ClockModule {

    @Provides @PerApp IClock provideClock(){
        return new Clock();
    }
    @Provides @PerApp IUniqueIdGenerator provideUniqueIdGenerator(){
        return new UUID();
    }
}
