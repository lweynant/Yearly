package com.lweynant.yearly.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.controller.add_event.AddBirthdayActivity;
import com.lweynant.yearly.controller.add_event.AddEventActivity;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.controller.show_event.ShowBirthdayActivity;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;

public class EventViewFactory implements IEventViewFactory {

    private static final int EVENT_LIST_ELEMENT_VIEW_TYPE = 0;
    private static final int BIRTHDAY_LIST_ELEMENT_VIEW_TYPE = 1;
    private static final int SEPARATOR_LIST_ELEMENT_VIEW_TYPE = 2;
    private final IStringResources rstring;
    private final IClock clock;

    public EventViewFactory(IStringResources rstring, IClock clock) {
        this.rstring = rstring;
        this.clock = clock;
    }

    @Override public IListElementView getListElementView(ViewGroup parent, int eventType) {
        if (eventType == BIRTHDAY_LIST_ELEMENT_VIEW_TYPE) {
            return new BirthdayListElementView(rstring, parent);
        }
        else if (eventType == EVENT_LIST_ELEMENT_VIEW_TYPE) {
            return new EventListElementView(rstring, parent);
        }
        else if (eventType == SEPARATOR_LIST_ELEMENT_VIEW_TYPE) {
            return new SeparatorListElementView(rstring, parent);
        }
        return null;
    }

    @Override public int getListElementViewType(ListEventsContract.ListItem listItem) {
        if (listItem.isEvent()){
            IEvent event = listItem.getEvent();
            if (event.getType().equals(Birthday.class.getCanonicalName())) {
                return BIRTHDAY_LIST_ELEMENT_VIEW_TYPE;
            }
            return EVENT_LIST_ELEMENT_VIEW_TYPE;
        }

        return SEPARATOR_LIST_ELEMENT_VIEW_TYPE;
    }

    @Override public IEventNotificationText getEventNotificationText(IEvent event) {
        if (event.getType().equals(Birthday.class.getCanonicalName())) {
            return new BirthdayNotificationText(event, new BirthdayStringResource(rstring), clock);
        }
        else if (event.getType().equals(Event.class.getCanonicalName())) {
            return new EventNotificationText(event, new EventStringResource(rstring), clock);
        }
        return null;
    }

    @Override public Intent getActivityIntentForEditing(Context context, Bundle bundle) {
        Intent intent = null;
        String type = bundle.getString(IEvent.KEY_TYPE);
        if(type.equals(Birthday.class.getCanonicalName())) {
            intent = new Intent(context, AddBirthdayActivity.class);
            intent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        }
        else if (type.equals(Event.class.getCanonicalName())) {
            intent = new Intent(context, AddEventActivity.class);
            intent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        }
        return intent;
    }

    @Override public Intent getActivityIntentForShowingEvent(Context context, Bundle bundle) {
        Intent intent = null;
        String type = bundle.getString(IEvent.KEY_TYPE);
        if(type.equals(Birthday.class.getCanonicalName())) {
            intent = new Intent(context, ShowBirthdayActivity.class);
            intent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        }
        else if (type.equals(Event.class.getCanonicalName())) {
            intent = new Intent(context, AddEventActivity.class);
            intent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        }
        return intent;
    }


}
