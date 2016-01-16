package com.lweynant.yearly.platform;

import com.lweynant.yearly.controller.AlarmGenerator;

public interface BasePlatformComponent {
    IClock clock();

    IUniqueIdGenerator uniqueIdGenerator();

    IJsonFileAccessor jsonFileAccessor();

    IAlarm alarm();

    IEventNotification eventNotification();
}
