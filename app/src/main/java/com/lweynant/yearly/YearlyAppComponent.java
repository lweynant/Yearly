package com.lweynant.yearly;

import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.model.ModelModule;
import com.lweynant.yearly.platform.PlatformComponent;
import com.lweynant.yearly.ui.ViewModule;

import dagger.Component;

@PerApp
@Component(dependencies = PlatformComponent.class, modules = {
        YearlyAppModule.class, SyncControllerModule.class, NotificationModule.class,
        ModelModule.class, ViewModule.class, ControllerModule.class})
public interface YearlyAppComponent extends BaseYearlyAppComponent {

}