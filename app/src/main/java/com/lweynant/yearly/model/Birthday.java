package com.lweynant.yearly.model;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;


public class Birthday implements IEvent {

    private final IRString rstring;
    private final int day;
    @Date.Month
    private final int month;
    private final IClock clock;
    private String name;

    public Birthday(String name, @Date.Month int month, int day, IClock clock, IRString rstring) {
        this.rstring = rstring;
        this.name = name;
        this.day = day;
        this.month = month;
        this.clock = clock;
    }


    @Override
    public String getTitle() {
        return String.format(rstring.getStringFromId(R.string.birthday_from), name);
    }

    @Override
    public LocalDate getDate() {
        LocalDate eventDate = new LocalDate(clock.now().getYear(), month, day);
        if (eventDate.isBefore(clock.now())){
            eventDate = eventDate.plusYears(1);
        }
        return eventDate;
    }

    @Override
    public int compareTo(IEvent another) {
        return getDate().compareTo(another.getDate());
    }
}
