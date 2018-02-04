package com.lweynant.yearly;

import android.content.Context;

import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.IRawAlarm;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class YearlyAppModule {
    private final YearlyApp app;

    public YearlyAppModule(YearlyApp app) {
        this.app = app;
    }

    @Provides @PerApp IStringResources providesRString() {
        return app;
    }
    @Provides @PerApp YearlyApp providesApp(){return app;}
    @Provides IDateFormatter providesDateFormatter(IStringResources rstring) {
        return new DateFormatter(rstring);
    }
    @Provides @PerApp IAlarm providesAlaram(IRawAlarm rawAlarm, IPreferences preferences, IDateFormatter dateFormatter, IClock clock) {
        return new AlarmArchiver(rawAlarm, preferences, dateFormatter, clock);
    }


}
