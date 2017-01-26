package com.lweynant.yearly.platform;



public interface BasePlatformComponent {
    IClock clock();

    IUniqueIdGenerator uniqueIdGenerator();

    IJsonFileAccessor jsonFileAccessor();

    IRawAlarm alarm();

    IEventNotification eventNotification();

    IPictureRepo pictureRepo();
    IPreferences preferences();
}
