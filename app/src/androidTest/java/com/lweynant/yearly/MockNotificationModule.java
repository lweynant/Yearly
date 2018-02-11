package com.lweynant.yearly;

import android.app.NotificationManager;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockNotificationModule {
    @Provides @PerApp NotificationManager providesNotificationManager(){
        return mock(NotificationManager.class);
    }
    @Provides @PerApp NotificationChannels providesNotificationChannels(){
        return mock(NotificationChannels.class);
    }
}
