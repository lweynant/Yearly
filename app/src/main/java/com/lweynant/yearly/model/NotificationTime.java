package com.lweynant.yearly.model;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class NotificationTime {
    public static final int MORNING = 6;
    public static final int EVENING = 19;
    private final int hour;
    private final LocalDate alarmDate;

    public NotificationTime(final LocalDate from, IEvent event) {
        LocalDate eventDate = event.getDate();
        if (eventDate.isBefore(from)) {
            eventDate = eventDate.plusYears(1);
        }
        int days = Days.daysBetween(from, eventDate).getDays();
        days = days - event.getNbrOfDaysForNotification();
        days = days < 0 ? 0 : days;
        hour = from.isEqual(eventDate) ? MORNING : EVENING;
        alarmDate = from.plusDays(days);
    }

    public final int getHour() {
        return hour;
    }

    public boolean isBefore(NotificationTime other) {
        if (alarmDate.isBefore(other.alarmDate)) return true;
        else if (alarmDate.isAfter(other.alarmDate)) return false;
        else return hour < other.hour;
    }

    public static NotificationTime min(NotificationTime rhs, NotificationTime lhs) {
        return rhs.isBefore(lhs) ? rhs : lhs;
    }

    public LocalDate getAlarmDate() {
        return alarmDate;
    }
}
