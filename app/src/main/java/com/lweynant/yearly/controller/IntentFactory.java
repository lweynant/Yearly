package com.lweynant.yearly.controller;

import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.controller.list_events.ListEventsActivity;
import com.lweynant.yearly.model.IEvent;

import timber.log.Timber;

public class IntentFactory implements IIntentFactory {


    private Context context;
    public IntentFactory(Context context){
        this.context = context;
    }

    @Override public Intent createNotificationIntent(IEvent event) {
        Timber.d("createNotificationIntent for event %s", event.toString());
        return new Intent(context, ListEventsActivity.class);
    }
}
