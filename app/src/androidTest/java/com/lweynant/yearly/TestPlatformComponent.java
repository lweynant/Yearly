package com.lweynant.yearly;

import com.lweynant.yearly.platform.BasePlatformComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MockPlatformModule.class)
public interface TestPlatformComponent extends BasePlatformComponent {

}
