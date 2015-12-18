package com.lweynant.yearly.ui;


import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEvent;

public class BirthdayStringResource {

    private final IRString rstring;

    BirthdayStringResource(IRString rstring){
        this.rstring = rstring;
    }

    public String getFormattedTitle(IEvent event){
        return String.format(rstring.getStringFromId(R.string.birthday_from), event.getTitle());
    }
    public String getStringFromId(int id) {
        return rstring.getStringFromId(id);
    }
}
