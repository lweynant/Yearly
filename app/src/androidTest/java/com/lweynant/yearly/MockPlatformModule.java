package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IPictureRepo;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.IRawAlarm;
import com.lweynant.yearly.platform.IUniqueIdGenerator;
import com.lweynant.yearly.platform.UUID;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockPlatformModule {
    @Provides @Singleton IClock provideClock() {
        return mock(IClock.class);
    }

    @Provides @Singleton IUniqueIdGenerator provideUniqueIdGenerator() {
        //take care this needs to be an uid that returns different values each time when it is asked
        // trouble is that it is not that easy to mock, for now it is fine to use the real uid generator
        return new UUID();
    }


    @Provides @Singleton IJsonFileAccessor provideJsonFileAccessor() {
        return mock(IJsonFileAccessor.class);
    }
    @Provides @Singleton IPreferences providePreferences() {
        return mock(IPreferences.class);
    }

    @Provides @Singleton IRawAlarm provideAlarm() {
        return mock(IRawAlarm.class);
    }


    @Provides @Singleton IEventNotification provideEventNotification() {
        return mock(IEventNotification.class);
    }
    @Provides @Singleton IPictureRepo providePictureRepo() {
        return mock(IPictureRepo.class);
    }

}
