package com.lweynant.yearly;

import com.google.gson.JsonObject;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IJsonFileWriter;

import java.io.IOException;

import rx.Subscriber;
import timber.log.Timber;

public class EventRepoSerializerToFileDecorator extends Subscriber<IEvent> {
    private final EventRepoSerializer serializer;
    private IJsonFileWriter accessor;

    public EventRepoSerializerToFileDecorator(IJsonFileWriter accessor, EventRepoSerializer eventRepoSerializer) {
        this.accessor = accessor;
        this.serializer = eventRepoSerializer;
    }

    @Override
    public void onCompleted() {
        Timber.d("onCompleted");
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
        Timber.d("onError %s", e.toString());
        serializer.onError(e);
        accessor = null;
    }

    @Override
    public void onNext(IEvent event) {
        Timber.d("onNext");
        serializer.onNext(event);
    }

}
