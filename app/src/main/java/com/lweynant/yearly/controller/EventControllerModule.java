package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.IComponentRegistry;
import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.platform.IClock;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module
public class EventControllerModule {


    @Provides DateFormatter providesDateFormatter(IStringResources rstring) {
        return new DateFormatter(rstring);
    }

    @Provides
    DateSelector providesDateSelector(IComponentRegistry componentRegistry, IClock clock) {
        return new DateSelector(componentRegistry, clock);
    }
    @Provides @Named("birthday_builder") Bundle providesBundle() {
        return new Bundle();
    }

    @Provides @Named("birthday_builder") Intent providesIntent() {
        return new Intent();
    }
}
