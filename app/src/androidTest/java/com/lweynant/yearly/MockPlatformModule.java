package com.lweynant.yearly;

import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;
import com.lweynant.yearly.util.UUID;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;

@Module
public class MockPlatformModule {
    @Provides
    @Singleton IClock provideClock() {
        return mock(IClock.class);
    }

    @Provides
    @Singleton IUniqueIdGenerator provideUniqueIdGenerator() {
        //take care this needs to be an uid that returns different values each time when it is asked
        // trouble is that it is not that easy to mock, for now it is fine to use the real uid generator
        return new UUID();
    }


    @Provides
    @Singleton IJsonFileAccessor provideJsonFileAccessor() {
        return mock(IJsonFileAccessor.class);
    }

}
