package com.lweynant.yearly.model;

import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import dagger.Module;
import dagger.Provides;


@Module
public class EventModelModule {


    @Provides @PerApp
    EventRepo provideEventRepo(IJsonFileAccessor fileAccessor, IClock clock, IUniqueIdGenerator idGenerator) {
        return new EventRepo(fileAccessor, clock, idGenerator);
    }

    @Provides
    BirthdayBuilder providesBirthdayBuilder(IClock clock, IUniqueIdGenerator idGenerator) {
        return new BirthdayBuilder(clock, idGenerator);
    }
}