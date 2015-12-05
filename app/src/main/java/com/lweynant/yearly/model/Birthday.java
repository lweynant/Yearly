package com.lweynant.yearly.model;

import com.google.gson.annotations.Expose;
import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;


public class Birthday extends Event {

    private final IRString rstring;
    @Expose
    private String name;

    public Birthday(String name, @Date.Month int month, int day, IClock clock, IRString rstring) {
        super(month, day, clock);
        this.rstring = rstring;
        this.name = name;
        super.setNbrOfDaysForNotification(2);
    }


    @Override
    public String getTitle() {
        return String.format(rstring.getStringFromId(R.string.birthday_from), name);
    }

}
