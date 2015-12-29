package com.lweynant.yearly.model;

import android.content.Context;

import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import dagger.Module;
import dagger.Provides;


@Module
public class EventRepoModule {
    private final Context context;

    public EventRepoModule(Context context){
        this.context = context;
    }
    @Provides @PerApp IJsonFileAccessor provideJsonFileAccessor() {
        return new EventRepoFileAccessor(context);
    }
    @Provides @PerApp EventRepo provideEventRepo(IJsonFileAccessor fileAccessor, IClock clock, IUniqueIdGenerator idGenerator){
        return new EventRepo(fileAccessor, clock, idGenerator);
    }
}
