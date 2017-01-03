package com.lweynant.yearly.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IEventNotificationText;

public interface IEventViewFactory {
    IListElementView getListElementView(ViewGroup parent, int eventType);

    int getListElementViewType(ListEventsContract.ListItem event);

    IEventNotificationText getEventNotificationText(IEvent event);

    Intent getActivityIntentForEditing(Context context, Bundle bundle);

    Intent getActivityIntentForShowingEvent(Context context, Bundle bundle);

}
