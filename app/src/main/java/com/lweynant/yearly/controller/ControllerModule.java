package com.lweynant.yearly.controller;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;

import dagger.Module;
import dagger.Provides;

@Module
public class ControllerModule {

    private final Context appContext;

    public ControllerModule(Application app) {
        this.appContext = app.getApplicationContext();
    }


    @Provides IIntentFactory providesIntentFactory() {
        return new IntentFactory(appContext);
    }

    @Provides DateFormatter providesDateFormatter(IStringResources rstring) {
        return new DateFormatter(rstring);
    }

    @Provides DateSelector providesDateSelector(IClock clock) {
        return new DateSelector(clock);
    }
    @Provides Bundle providesBundle() {
        return new Bundle();
    }

    @Provides Intent providesIntent() {
        return new Intent();
    }

    @Provides @PerApp EventNotifier providesEventNotifier(IEventNotification eventNotification,
                                                          IIntentFactory intentFactory,
                                                          IEventViewFactory viewFactory,
                                                          IClock clock) {

        return new EventNotifier(eventNotification, intentFactory, viewFactory, clock);
    }

    @Provides @PerApp AddBirthdayContract.UserActionsListener providesAddBirthdayPresenter(BirthdayBuilder builder,
                                                                                           DateFormatter dateFormatter,
                                                                                           Bundle bundle,
                                                                                           Intent resultIntent) {
        return new AddBirthdayPresenter(builder, dateFormatter, bundle, resultIntent);
    }
}
