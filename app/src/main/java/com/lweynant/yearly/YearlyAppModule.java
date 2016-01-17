package com.lweynant.yearly;

import android.content.Context;

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

}
