package com.lweynant.yearly.util;

import com.lweynant.yearly.model.IJsonFileAccessor;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = PlatformModule.class)
public interface PlatformComponent extends BasePlatformComponent {

}
