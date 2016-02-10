package com.lweynant.yearly.ui;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.model.IEvent;

public class EventStringResource implements IEventStringResource {
    private IStringResources rstring;

    public EventStringResource(IStringResources rstring) {
        this.rstring = rstring;
    }

    @Override public String getFormattedTitle(IEvent event) {
        return event.getName();
    }

    @Override public String getStringFromId(int id) {
        return rstring.getString(id);
    }
}
