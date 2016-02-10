package com.lweynant.yearly.ui;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.utils.CaseFormat;

public class EventNotificationText extends NotificationText {

    public EventNotificationText(IEvent event, IEventStringResource rstring, IClock clock) {
        super(event, rstring, clock);
    }

    @Override public String getTitle() {
        return CaseFormat.capitalizeFirstLetter(event.getName());
    }

}
