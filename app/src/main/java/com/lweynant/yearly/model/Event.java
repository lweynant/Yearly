package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class Event implements IEvent {
    private final int day;
    @Date.Month
    private final int month;
    private final IClock clock;
    private int nbrDaysForNotification = 1;

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
        if (eventDate.isBefore(clock.now())) {
            eventDate = eventDate.plusYears(1);
        }
        return eventDate;
    }

    @Override
    public int getNbrOfDaysForNotification() {
        return nbrDaysForNotification;
    }

    @Override
    public void setNbrOfDaysForNotification(int days) {
        nbrDaysForNotification = days;
    }


    @Override
    public int compareTo(IEvent another) {
        return getDate().compareTo(another.getDate());
    }

    public static TimeBeforeNotification daysBeforeNotification(LocalDate from, IEvent event) {
        LocalDate eventDate = event.getDate();
        if (eventDate.isBefore(from)) {
            eventDate = eventDate.plusYears(1);
        }
        int days = Days.daysBetween(from, eventDate).getDays();
        if (days > 0) {
            days = days - event.getNbrOfDaysForNotification();
            days = days < 0 ? 0 : days;
        }
        int morning = 6;
        int evening = 19;
        int hour = from.isEqual(eventDate)? morning : evening;

        return new TimeBeforeNotification(days, hour);
    }

    public static boolean shouldBeNotified(LocalDate from, IEvent event) {
        int days = Days.daysBetween(from, event.getDate()).getDays();
        return days >= 0 && days <= event.getNbrOfDaysForNotification();
    }
}
