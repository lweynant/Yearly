package com.lweynant.yearly.model;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;


public class Birthday implements IEventType {

    private final IRString rstring;
    private final int day;
    @Date.Month
    private final int month;
    private String name;

    public Birthday(String name, int day, @Date.Month int month, IRString rstring) {
        this.rstring = rstring;
        this.name = name;
        this.day = day;
        this.month = month;
    }

    @Override
    public String getTitle() {
        return String.format(rstring.getStringFromId(R.string.birthday_title), name);
    }

    @Override
    public int getDay() {
        return day;
    }

    @Override
    public int getMonth() {
        return month;
    }
}
