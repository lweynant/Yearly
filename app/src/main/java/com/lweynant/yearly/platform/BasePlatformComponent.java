package com.lweynant.yearly.platform;


public interface BasePlatformComponent {
    IClock clock();

    IUniqueIdGenerator uniqueIdGenerator();

    IJsonFileAccessor jsonFileAccessor();

    IAlarm alarm();

    IEventNotification eventNotification();
}
