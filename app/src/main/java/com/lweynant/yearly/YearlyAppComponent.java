package com.lweynant.yearly;

import com.lweynant.yearly.controller.EventControllerModule;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.model.EventModelModule;
import com.lweynant.yearly.platform.PlatformComponent;
import com.lweynant.yearly.ui.EventViewModule;

import dagger.Component;

@PerApp
@Component(dependencies = PlatformComponent.class, modules = {
        YearlyAppModule.class, SyncControllerModule.class, EventModelModule.class, EventViewModule.class, EventControllerModule.class})
public interface YearlyAppComponent extends BaseYearlyAppComponent {

}