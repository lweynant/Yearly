package com.lweynant.yearly.platform;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventNotificationText;

public interface IEventNotification {
    void notify(IEvent event, IEventNotificationText text);

}
