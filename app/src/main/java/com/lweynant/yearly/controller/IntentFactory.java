package com.lweynant.yearly.controller;

import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.IEvent;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

public class IntentFactory implements IIntentFactory {


    private Context context;
    public IntentFactory(Context context){
        this.context = context;
    }

    @Override public Intent createNotificationIntent(IEvent event) {
        Timber.d("createNotificationIntent for event %s", event.toString());
        return new Intent(context, EventsActivity.class);
    }
}
