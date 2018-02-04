package com.lweynant.yearly.platform;


import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.AlarmReceiver;
import com.lweynant.yearly.EventNotification;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PlatformModule {
    private final Context context;

    public PlatformModule(Application application) {
        //we pass the application and retrieve the context here, this way
        // we are sure that we use the application context. We need the
        // application context because this is singleton that outlives
        // activities
        this.context = application.getApplicationContext();
    }

    @Provides @Singleton IJsonFileAccessor provideJsonFileAccessor() {
        return new JsonFileAccessor(context, "events.json");
    }

    @Provides
    @Singleton IClock provideClock() {
        return new Clock();
    }

    @Provides
    @Singleton IUniqueIdGenerator provideUniqueIdGenerator() {
        return new UUID();
    }


    @Provides @Singleton IPreferences providePreferences(){
        return new Preferences(context);
    }

    @Provides @Singleton IRawAlarm provideAlarm() {
        return new Alarm(context, new Intent(context, AlarmReceiver.class));
    }
}
