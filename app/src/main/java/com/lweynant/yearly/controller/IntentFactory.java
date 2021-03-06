package com.lweynant.yearly.controller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.controller.list_events.ListEventsActivity;
import com.lweynant.yearly.controller.show_event.ShowBirthdayActivity;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.IEvent;

import timber.log.Timber;

public class IntentFactory implements IIntentFactory {


    private Context context;
    public IntentFactory(Context context){
        this.context = context;
    }

    @Override public Intent createNotificationIntent(IEvent event) {
        Timber.d("createNotificationIntent for event %s", event.toString());
        if (event.getType().equals(Birthday.class.getCanonicalName())) {
            Bundle bundle = new Bundle();
            event.archiveTo(bundle);
            Intent intent = new Intent(context, ShowBirthdayActivity.class);
            intent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
            return intent;
        }
        return new Intent(context, ListEventsActivity.class);
    }
}
