package com.lweynant.yearly;

import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockClockModule {
    @Provides
    @Singleton
    IClock provideClock() {
        return mock(IClock.class);
    }

    @Provides
    @Singleton
    IUniqueIdGenerator provideUniqueIdGenerator() {
        return mock(IUniqueIdGenerator.class);
    }
}
