package com.lweynant.yearly.platform;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = PlatformModule.class)
public interface PlatformComponent extends BasePlatformComponent {

}
