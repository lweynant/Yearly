package com.lweynant.yearly.model;

import dagger.Module;
import dagger.Provides;
import android.content.Context;

import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;
import com.lweynant.yearly.util.UUID;

import javax.inject.Singleton;

@Module
public class EventRepoModule {
    private final Context context;

    public EventRepoModule(Context context){
        this.context = context;
    }
    @Provides @Singleton IJsonFileAccessor provideJsonFileAccessor() {
        return new EventRepoFileAccessor(context);
    }
    @Provides @Singleton EventRepo provideEventRepo(IJsonFileAccessor fileAccessor, IClock clock, IUniqueIdGenerator idGenerator){
        return new EventRepo(fileAccessor, clock, idGenerator);
    }
}
