package com.lweynant.yearly.platform;


import android.content.Context;

import com.lweynant.yearly.model.EventRepoFileAccessor;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.ui.EventViewFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlatformModule {
    private final Context context;

    public PlatformModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton IJsonFileAccessor provideJsonFileAccessor() {
        return new EventRepoFileAccessor(context);
    }

    @Provides
    @Singleton IClock provideClock() {
        return new Clock();
    }

    @Provides
    @Singleton IUniqueIdGenerator provideUniqueIdGenerator() {
        return new UUID();
    }

    @Provides @Singleton IAlarm provideAlarm() {
        return new Alarm(context);
    }
    @Provides @Singleton AlarmGenerator providesAlarmGenerator(IAlarm alarm) {
        return new AlarmGenerator(alarm);
    }
    @Provides @Singleton IEventNotification provideEventNotification(IClock clock) {
        return new EventNotification(context, clock);
    }
}
