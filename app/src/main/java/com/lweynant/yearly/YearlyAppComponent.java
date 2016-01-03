package com.lweynant.yearly;

import com.lweynant.yearly.controller.EventControllerModule;
import com.lweynant.yearly.controller.EventsAdapterModule;
import com.lweynant.yearly.model.EventModelModule;
import com.lweynant.yearly.ui.EventViewModule;
import com.lweynant.yearly.util.PlatformComponent;

import dagger.Component;

@PerApp
@Component(dependencies = PlatformComponent.class, modules = {
        YearlyAppModule.class, EventsAdapterModule.class, EventModelModule.class, EventViewModule.class, EventControllerModule.class})
public interface YearlyAppComponent extends BaseYearlyAppComponent {

}