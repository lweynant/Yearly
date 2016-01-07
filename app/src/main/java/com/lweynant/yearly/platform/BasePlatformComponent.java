package com.lweynant.yearly.platform;

import com.lweynant.yearly.model.IJsonFileAccessor;

public interface BasePlatformComponent {
    IClock clock();

    IUniqueIdGenerator uniqueIdGenerator();

    IJsonFileAccessor jsonFileAccessor();
}
