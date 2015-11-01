package com.lweynant.yearly;

import android.app.Application;


import net.danlew.android.joda.JodaTimeAndroid;

import timber.log.Timber;

public class YearlyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.d("onCreate");
        JodaTimeAndroid.init(this);
    }
}
