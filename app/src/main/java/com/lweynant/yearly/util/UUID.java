package com.lweynant.yearly.util;

public class UUID implements IUUID{

    @Override
    public String getRandomUID() {
        return java.util.UUID.randomUUID().toString();
    }

    @Override
    public int hashCode(String uuid) {
        return java.util.UUID.fromString(uuid).hashCode();
    }
}
