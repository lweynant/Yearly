package com.lweynant.yearly.utils;

import android.content.Context;

import com.google.gson.JsonObject;
import com.lweynant.yearly.platform.IJsonFileWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import timber.log.Timber;


public class JsonStreamWriter implements IJsonFileWriter {

    private final OutputStream outputStream;

    public JsonStreamWriter(OutputStream outputStream){
        this.outputStream = outputStream;
    }
    @Override public void write(JsonObject content) throws IOException {
        Timber.d("write file");
        synchronized (this) {

            outputStream.write(content.toString().getBytes());
            outputStream.close();
        }

    }
}
