package com.lweynant.yearly.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lweynant.yearly.util.IClock;

import rx.Subscriber;
import timber.log.Timber;

public class EventRepoSerializer extends Subscriber<IEvent> {


    public static final String VERSION = "version";
    public static final String TYPE = "type";
    public static final String SERIALIZED_ON = "serialized_on";
    public static final String EVENTS = "events";
    private final IClock clock;
    JsonArray jsonEventsArray = new JsonArray();
    GsonBuilder builder = new GsonBuilder();
    private JsonObject json = null;

    public EventRepoSerializer(IClock clock) {
        this.clock = clock;
        builder.excludeFieldsWithoutExposeAnnotation();
    }


    @Override
    public void onCompleted() {
        Timber.d("onCompleted");
        json = new JsonObject();
        json.addProperty(VERSION, "1.0");
        json.addProperty(TYPE, getClass().getCanonicalName());
        json.addProperty(SERIALIZED_ON, clock.timestamp());
        json.add(EVENTS, jsonEventsArray);
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "onError");
    }

    @Override
    public void onNext(IEvent event) {
        Timber.d("onNext %s", event.toString());
        jsonEventsArray.add(builder.create().toJsonTree(event));
    }

    public String serialized() {
        return json.toString();
    }

    public boolean isSerialized() {
        return json != null;
    }
}
