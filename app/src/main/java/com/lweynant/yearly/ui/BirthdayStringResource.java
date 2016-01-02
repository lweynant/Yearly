package com.lweynant.yearly.ui;


import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEvent;

public class BirthdayStringResource {

    private final IStringResources rstring;

    BirthdayStringResource(IStringResources rstring) {
        this.rstring = rstring;
    }

    public String getFormattedTitle(IEvent event) {
        return String.format(rstring.getString(R.string.birthday_from), event.getName());
    }

    public String getStringFromId(int id) {
        return rstring.getString(id);
    }
}
