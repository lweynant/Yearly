package com.lweynant.yearly.util;

import com.lweynant.yearly.model.IJsonFileAccessor;

public interface BasePlatformComponent {
    IClock clock();

    IUniqueIdGenerator uniqueIdGenerator();

    IJsonFileAccessor jsonFileAccessor();
}
