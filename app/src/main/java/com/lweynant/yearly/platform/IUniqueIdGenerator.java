package com.lweynant.yearly.platform;

public interface IUniqueIdGenerator {
    String getUniqueId();

    int hashCode(String uuid);
}
