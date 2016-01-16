package com.lweynant.yearly.ui;

import android.view.ViewGroup;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;

public class EventViewFactory implements IEventViewFactory {

    private static final int EVENT_LIST_ELEMENT_VIEW_TYPE = 0;
    private static final int BIRTHDAY_LIST_ELEMENT_VIEW_TYPE = 1;
    private final IStringResources rstring;
    private final IClock clock;

    public EventViewFactory(IStringResources rstring, IClock clock) {
        this.rstring = rstring;
        this.clock = clock;
    }

    @Override public IEventListElementView getEventListElementView(ViewGroup parent, int eventType) {
        if (eventType == BIRTHDAY_LIST_ELEMENT_VIEW_TYPE) {
            return new BirthdayListElementView(rstring, parent);
        }
        return null;
    }

    @Override public int getEventListElementViewType(IEvent event) {
        if (event.getType().equals(Birthday.class.getCanonicalName())) {
            return BIRTHDAY_LIST_ELEMENT_VIEW_TYPE;
        }
        return EVENT_LIST_ELEMENT_VIEW_TYPE;
    }

    @Override public IEventNotificationText getEventNotificationText(IEvent event) {
        if (event.getType().equals(Birthday.class.getCanonicalName())) {
            return new BirthdayEventNotificationText(event, rstring, clock);
        }
        return null;
    }

}
