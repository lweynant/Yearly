package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.platform.IClock;

import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

class WeekChecker {
    private IClock clock;

    public WeekChecker(IClock clock) {

        this.clock = clock;
    }

    public boolean isBetween(LocalDate date, LocalDate begin, LocalDate end) {
        if ((date.isAfter(begin) || date.isEqual(begin))
                && (date.isBefore(end) || date.isEqual(end)))
            return true;
        return false;
    }

    public boolean isThisWeek(LocalDate date) {
        LocalDate now = clock.now();
        LocalDate beginOfWeek = now.withDayOfWeek(DateTimeConstants.MONDAY);
        LocalDate endOfWeek = now.withDayOfWeek(DateTimeConstants.SUNDAY);
        return isBetween(date, beginOfWeek, endOfWeek);
    }

    public boolean isNextWeek(LocalDate date) {
        LocalDate now = clock.now();
        LocalDate beginOfWeek = now.withDayOfWeek(DateTimeConstants.MONDAY).plusWeeks(1);
        LocalDate endOfWeek = now.withDayOfWeek(DateTimeConstants.SUNDAY).plusWeeks(1);
        return isBetween(date, beginOfWeek, endOfWeek);
    }
}
