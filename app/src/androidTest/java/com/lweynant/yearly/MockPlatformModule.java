package com.lweynant.yearly;

import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockPlatformModule {
    @Provides
    @Singleton IClock provideClock() {
        return mock(IClock.class);
    }

    @Provides
    @Singleton IUniqueIdGenerator provideUniqueIdGenerator() {
        return mock(IUniqueIdGenerator.class);
    }


    @Provides
    @Singleton IJsonFileAccessor provideJsonFileAccessor() {
        return mock(IJsonFileAccessor.class);
    }

}
