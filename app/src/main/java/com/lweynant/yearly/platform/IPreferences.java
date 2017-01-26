package com.lweynant.yearly.platform;

public interface IPreferences {

    void setStringValue(String key, String value);
    String getStringValue(String key, String defaultValue);

    void remove(String key);
}
