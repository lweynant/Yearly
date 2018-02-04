package com.lweynant.yearly.platform;



public interface BasePlatformComponent {
    IClock clock();

    IUniqueIdGenerator uniqueIdGenerator();

    IJsonFileAccessor jsonFileAccessor();

    IRawAlarm alarm();


    IPreferences preferences();
}
