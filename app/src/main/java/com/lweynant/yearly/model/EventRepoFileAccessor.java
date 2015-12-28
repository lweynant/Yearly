package com.lweynant.yearly.model;

import android.content.Context;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import timber.log.Timber;

public class EventRepoFileAccessor implements IJsonFileAccessor {
    private final String filename;
    private final Context context;

    public EventRepoFileAccessor(Context context) {
        Timber.d("build EventRepoFileAccessor instance");
        this.filename = "events.json";
        this.context = context;
    }

    @Override
    public void write(JsonObject content) throws IOException {
        Timber.d("write file %s", filename);
        synchronized (this) {
            FileOutputStream outputStream;

            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(content.toString().getBytes());
            outputStream.close();
            Timber.d("written file %s", context.getFileStreamPath(filename));
        }
    }

    @Override
    public JsonObject read() throws IOException {
        Timber.d("reading file %s", filename);
        synchronized (this) {
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(context.openFileInput(filename)));
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(in).getAsJsonObject();
                return jsonObject;

            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }
}
