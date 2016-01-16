package com.lweynant.yearly.ui;

import android.view.ViewGroup;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IEventNotificationText;

public interface IEventViewFactory {
    IEventListElementView getEventListElementView(ViewGroup parent, int eventType);

    int getEventListElementViewType(IEvent event);

    IEventNotificationText getEventNotificationText(IEvent event);
}
