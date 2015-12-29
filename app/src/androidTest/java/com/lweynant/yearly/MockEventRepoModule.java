package com.lweynant.yearly;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import dagger.Module;
import dagger.Provides;

import static org.mockito.Mockito.mock;

@Module
public class MockEventRepoModule {
    @Provides
    @PerApp
    IJsonFileAccessor provideJsonFileAcessor()
    {
        return mock(IJsonFileAccessor.class);
    }
    @Provides
    @PerApp
    EventRepo provideEventRepo(IJsonFileAccessor fileAccessor, IClock clock, IUniqueIdGenerator idGenerator){
        return new EventRepo(fileAccessor, clock, idGenerator);
    }
}
