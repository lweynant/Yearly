package com.lweynant.yearly.util;

import com.google.gson.JsonObject;
import com.lweynant.yearly.model.EventRepoFileAccessor;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;

import java.io.IOException;

import rx.Subscriber;

public class EventRepoSerializerToFileDecorator extends Subscriber<IEvent> {
    private EventRepoFileAccessor accessor;
    private final EventRepoSerializer serializer;

    public EventRepoSerializerToFileDecorator(EventRepoFileAccessor accessor, EventRepoSerializer eventRepoSerializer) {
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
