package com.lweynant.yearly.platform;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface IJsonFileWriter {
    void write(JsonObject content) throws IOException;
}
