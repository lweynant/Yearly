package com.lweynant.yearly.model;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;

import org.joda.time.LocalDate;

public class Birthday implements IEventType {

    private final IRString rstring;
    private String name;
    private LocalDate date;

    public Birthday(String name, LocalDate date, IRString rstring) {
        this.rstring = rstring;
        this.name = name;
        this.date = date;
    }

    @Override
    public String getTitle() {
        return String.format(rstring.getStringFromId(R.string.birthday_title), name);
    }

    @Override
    public LocalDate getDate() {
        return date;
    }
}
