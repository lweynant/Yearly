package com.lweynant.yearly.model;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface IJsonFileAccessor {

    void write(JsonObject content) throws IOException;

    JsonObject read() throws IOException;
}
