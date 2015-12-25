package com.lweynant.yearly.util;

public class UUID implements IUniqueIdGenerator {

    @Override
    public String getUniqueId() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public int hashCode(String uuid) {
        return java.util.UUID.fromString(uuid).hashCode();
    }
}
