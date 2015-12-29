package com.lweynant.yearly.util;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ClockModule.class)
public interface ClockComponent {
    public IClock clock();

    public IUniqueIdGenerator uniqueIdGenerator();
}
