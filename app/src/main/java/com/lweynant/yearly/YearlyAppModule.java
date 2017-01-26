package com.lweynant.yearly;

import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.IRawAlarm;

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
    @Provides IDateFormatter providesDateFormatter(IStringResources rstring) {
        return new DateFormatter(rstring);
    }
    @Provides @PerApp IAlarm providesAlaram(IRawAlarm rawAlarm, IPreferences preferences, IDateFormatter dateFormatter) {
        return new AlarmArchiver(rawAlarm, preferences, dateFormatter);
    }


}
