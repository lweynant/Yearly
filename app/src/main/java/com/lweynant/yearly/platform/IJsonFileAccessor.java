package com.lweynant.yearly.platform;

import com.google.gson.JsonObject;

import java.io.IOException;

public interface IJsonFileAccessor extends IJsonFileWriter {

    JsonObject read() throws IOException;
}
