package com.lweynant.yearly;

import com.lweynant.yearly.model.IEvent;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import rx.functions.Func1;

public class FilterEventsInRange implements Func1<IEvent, Boolean> {
    private final LocalDate now;
    private final int range;

    public FilterEventsInRange(LocalDate now, int range) {
        this.now = now;
        this.range = range;
    }

    @Override
    public Boolean call(IEvent event) {
        int days = Days.daysBetween(now, event.getDate()).getDays();
        return days >= 0 && days <= range;
    }
}
