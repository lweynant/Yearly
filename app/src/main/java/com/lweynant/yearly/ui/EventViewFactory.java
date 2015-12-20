package com.lweynant.yearly.ui;

import android.view.ViewGroup;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.util.IClock;

public class EventViewFactory {

    private static final int EVENT_LIST_ELEMENT_VIEW_TYPE = 0;
    private static final int BIRTHDAY_LIST_ELEMENT_VIEW_TYPE = 1;
    private final IRString rstring;
    private final IClock clock;

    public EventViewFactory(IRString rstring, IClock clock){
        this.rstring = rstring;
        this.clock = clock;
    }
    public IEventListElementView getEventListElementView(IEvent event) {
        return null;
    }

    public IEventListElementView getEventListElementView(ViewGroup parent, int eventType) {
        if (eventType == BIRTHDAY_LIST_ELEMENT_VIEW_TYPE){
            return new BirthdayListElementView(rstring, parent);
        }
        return null;
    }

    public int getEventListElementViewType(IEvent event) {
        if (event.getType().equals(Birthday.class.getCanonicalName())){
           return BIRTHDAY_LIST_ELEMENT_VIEW_TYPE;
        }
        return EVENT_LIST_ELEMENT_VIEW_TYPE;
    }

    public IEventNotificationText getEventNotificationText(IEvent event){
        if (event.getType().equals(Birthday.class.getCanonicalName())){
            return new BirthdayEventNotificationText(event, rstring, clock);
        }
        return null;
    }

}