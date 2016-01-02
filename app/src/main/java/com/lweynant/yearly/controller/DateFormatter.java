package com.lweynant.yearly.controller;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.Date;

public class DateFormatter {
    private final IStringResources rstring;

    public DateFormatter(IStringResources rstring) {
        this.rstring = rstring;
    }

    public String format(int year, @Date.Month int month, int day) {
        return rstring.getString(R.string.yyy_mm_dd, year, month, day);
    }

    public String format(@Date.Month int month, int day) {
        String[] months = rstring.getStringArray(R.array.months_day);
        return String.format(months[month], day);
    }
}
