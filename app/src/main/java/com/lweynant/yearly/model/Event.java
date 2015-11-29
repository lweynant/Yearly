package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

public class Event implements IEvent {
    private final int day;
    @Date.Month
    private final int month;
    private final IClock clock;

    public Event(@Date.Month int month, int day, IClock clock) {
        this.day = day;
        this.month = month;
        this.clock = clock;

    }

    @Override
    public String toString() {
        return getDate().toString();
    }

    @Override
    public int hashCode() {
        return getDate().hashCode();
    }

    @Override
    public String getTitle() {
        return "";
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
