package com.lweynant.yearly.platform;

import com.google.gson.JsonObject;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IJsonFileAccessor;

import java.io.IOException;

import rx.Subscriber;

public class EventRepoSerializerToFileDecorator extends Subscriber<IEvent> {
    private final EventRepoSerializer serializer;
    private IJsonFileAccessor accessor;

    public EventRepoSerializerToFileDecorator(IJsonFileAccessor accessor, EventRepoSerializer eventRepoSerializer) {
        this.accessor = accessor;
        this.serializer = eventRepoSerializer;
    }

    @Override
    public void onCompleted() {
        serializer.onCompleted();
        if (serializer.isSerialized()) {
            JsonObject json = serializer.serialized();
            try {
                accessor.write(json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        accessor = null;
    }

    @Override
    public void onError(Throwable e) {
        serializer.onError(e);
        accessor = null;
    }

    @Override
    public void onNext(IEvent event) {
        serializer.onNext(event);
    }

}
