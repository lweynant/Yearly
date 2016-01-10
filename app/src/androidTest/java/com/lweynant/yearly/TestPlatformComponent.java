package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.platform.BasePlatformComponent;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = MockPlatformModule.class)
public interface TestPlatformComponent extends BasePlatformComponent {

    CountingIdlingResource countingIdlingResource();
}
