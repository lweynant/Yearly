package com.lweynant.yearly.model;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lweynant.yearly.util.IClock;

import rx.Subscriber;
import timber.log.Timber;

public class EventRepoSerializer extends Subscriber<IEvent> {


    private final IClock clock;
    JsonArray jsonEventsArray = new JsonArray();
    GsonBuilder builder = new GsonBuilder();
    private boolean first = true;
    private JsonObject json = new JsonObject();
    private boolean serialized;

    public EventRepoSerializer(IClock clock) {
        this.clock = clock;
        builder.excludeFieldsWithoutExposeAnnotation();
        json.addProperty("version", "1.0");
        json.addProperty("type", getClass().getCanonicalName());
        json.add("events", jsonEventsArray);
    }


    @Override
    public void onCompleted() {
        Timber.d("onCompleted");
        json.addProperty("serialized_on", clock.timestamp());
        serialized = true;
    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "onError");
        serialized = false;
    }

    @Override
    public void onNext(IEvent event) {
        Timber.d("onNext %s", event.toString());
        if (first) {
            first = false;

        }
        jsonEventsArray.add(builder.create().toJsonTree(event));
    }

    public String serialized() {
        return json.toString();
    }

    public boolean isSerialized() {
        return serialized;
    }
}
