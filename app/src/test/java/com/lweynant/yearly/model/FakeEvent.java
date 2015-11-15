package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

public class FakeEvent implements IEvent {
    private final IClock clock;
    private int day;
    private
    @Date.Month
    int month;

    public FakeEvent(@Date.Month int month, int day, IClock clock) {
        this.day = day;
        this.month = month;
        this.clock = clock;
    }

    @Override
    public String toString() {
        return String.format("%d-%d", day, month);
    }

    @Override
    public String getTitle() {
        return "fake";
    }

    @Override
    public LocalDate getDate() {
        return new LocalDate(clock.now().getYear(), month, day);
    }
}