package com.lweynant.yearly.util;

public interface IUniqueIdGenerator {
    String getRandomUID();
    int hashCode(String uuid);
}
