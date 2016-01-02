package com.lweynant.yearly;

import com.lweynant.yearly.util.BasePlatformComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MockPlatformModule.class)
public interface TestPlatformComponent extends BasePlatformComponent {

}
