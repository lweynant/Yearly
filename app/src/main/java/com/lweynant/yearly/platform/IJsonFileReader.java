package com.lweynant.yearly.platform;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface IJsonFileReader {
    JsonObject read() throws IOException;
}
