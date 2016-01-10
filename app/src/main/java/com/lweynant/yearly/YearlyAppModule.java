package com.lweynant.yearly;

import android.content.Context;

import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;

import dagger.Module;
import dagger.Provides;

@Module
public class YearlyAppModule {
    private final YearlyApp app;

    public YearlyAppModule(YearlyApp app) {
        this.app = app;
    }

    @Provides @PerApp IComponentRegistry providesComponentRegistry() {
        return app;
    }

    @Provides @PerApp Context providesContext() {
        return app;
    }

    @Provides @PerApp IStringResources providesRString() {
        return app;
    }


}
