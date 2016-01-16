package com.lweynant.yearly.model;

import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import dagger.Module;
import dagger.Provides;


@Module
public class EventModelModule {


    @Provides @PerApp
    EventRepo provideEventRepo(IJsonFileAccessor fileAccessor, IClock clock, IUniqueIdGenerator idGenerator) {
        return new EventRepo(fileAccessor, clock, idGenerator);
    }


    @Provides EventRepoTransaction providesEventRepoTransaction(EventRepo repoModifier){
        return new EventRepoTransaction(repoModifier);
    }

    @Provides
    BirthdayBuilder providesBirthdayBuilder(IClock clock, IUniqueIdGenerator idGenerator) {
        return new BirthdayBuilder(clock, idGenerator);
    }
}
