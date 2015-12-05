package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import rx.Subscriber;

public class EventRepoSerializer extends Subscriber<IEvent> {

    StringBuilder serialized = new StringBuilder("[");
    GsonBuilder builder = new GsonBuilder();
    private boolean first = true;

    public EventRepoSerializer(){
        builder.excludeFieldsWithoutExposeAnnotation();
    }


    @Override
    public void onCompleted() {
        serialized.append("]");
    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(IEvent event) {
        if (first){
            first = false;
        }
        else{
            serialized.append(",");
        }
        serialized.append(builder.create().toJson(event));
    }

    public String serialized() {
        return serialized.toString();
    }
}
