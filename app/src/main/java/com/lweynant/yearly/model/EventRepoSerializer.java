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
    }


    @Override
    public void onCompleted() {
        Timber.d("onCompleted");
        handleFirst();
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
        handleFirst();
        jsonEventsArray.add(builder.create().toJsonTree(event));
    }

    private void handleFirst() {
        if (first) {
            first = false;
            builder.excludeFieldsWithoutExposeAnnotation();
            json.addProperty("version", "1.0");
            json.addProperty("type", getClass().getCanonicalName());
            json.addProperty("serialized_on", clock.timestamp());
            json.add("events", jsonEventsArray);
        }
    }

    public String serialized() {
        return json.toString();
    }

    public boolean isSerialized() {
        return serialized;
    }
}
