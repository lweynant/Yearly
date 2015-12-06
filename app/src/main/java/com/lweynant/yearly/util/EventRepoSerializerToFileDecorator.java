package com.lweynant.yearly.util;

import android.content.Context;

import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;

import java.io.FileOutputStream;

import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import timber.log.Timber;

public class EventRepoSerializerToFileDecorator extends Subscriber<IEvent> {
    private Context context;
    private final EventRepoSerializer serializer;

    public EventRepoSerializerToFileDecorator(Context context, EventRepoSerializer eventRepoSerializer) {
        this.context = context;
        this.serializer = eventRepoSerializer;
    }

    @Override
    public void onCompleted() {
        serializer.onCompleted();
        String filename = "events.json";
        String string = serializer.serialized();
        FileOutputStream outputStream;

        try {
            outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Timber.d("written file %s", context.getFileStreamPath(filename));
        context = null;
    }

    @Override
    public void onError(Throwable e) {
        serializer.onError(e);
        context = null;
    }

    @Override
    public void onNext(IEvent event) {
        serializer.onNext(event);
    }
}
