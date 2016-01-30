package com.lweynant.yearly.model;

import com.lweynant.yearly.PerApp;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import dagger.Module;
import dagger.Provides;


@Module
public class ModelModule {

    @Provides @PerApp EventRepo provideEventRepo(IJsonFileAccessor fileAccessor, IClock clock, IUniqueIdGenerator idGenerator) {
        return new EventRepo(fileAccessor, clock, idGenerator);
    }

    //EventRepo implements 2 interfaces, we make sure that both map to the same instance:
    @Provides IEventRepo provideIEventRepo(EventRepo repo) {
        return repo;
    }
    @Provides IEventRepoModifier provideIEventRepoModifier(EventRepo repo) {
        return repo;
    }


    @Provides IEventRepoTransaction providesEventRepoTransaction(IEventRepoModifier repoModifier){
        return new EventRepoTransaction(repoModifier);
    }

    @Provides @PerApp ValidatorFactory providesValidatorFactory() {
        return new ValidatorFactory();
    }
    @Provides IValidator providesEventValidator(ValidatorFactory factory) {
        return factory.create();
    }
    @Provides IKeyValueArchiver providesKeyValueStore(ValidatorFactory factory) {
        return new KeyValueArchiver(factory);
    }
    @Provides BirthdayBuilder providesBirthdayBuilder(IValidator validator,
                                                      IKeyValueArchiver keyValueStore,
                                                      IClock clock, IUniqueIdGenerator idGenerator) {
        return new BirthdayBuilder(validator, keyValueStore, clock, idGenerator);
    }
}
