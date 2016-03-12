package com.lweynant.yearly.model;

import com.lweynant.yearly.platform.IClock;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class NotificationTime {
    public static final int MORNING = 6;
    public static final int EVENING = 19;
    public static final int START_OF_DAY = -1;
    private final int hour;
    private final LocalDate alarmDate;

    public NotificationTime(LocalDate fromDay, int fromHour, IEvent event) {
        LocalDate eventDate = event.getDate();
        if (isEventPast(fromDay, fromHour, eventDate)) {
            eventDate = eventDate.plusYears(1);
        }
        if (fromHour >= EVENING) {
            fromDay = fromDay.plusDays(1);
        }
        int days = Days.daysBetween(fromDay, eventDate).getDays();
        days = days - event.getNbrOfDaysForNotification();
        days = days < 0 ? 0 : days;
        hour = fromDay.isEqual(eventDate) ? MORNING : EVENING;
        alarmDate = fromDay.plusDays(days);
    }

    private boolean isEventPast(LocalDate fromDay, int fromHour, LocalDate eventDate) {
        if (eventDate.isBefore(fromDay))  return true;
        if (eventDate.isEqual(fromDay) && fromHour >= MORNING) return true;
        return false;
    }

    public static NotificationTime min(NotificationTime rhs, NotificationTime lhs) {
        return rhs.isBefore(lhs) ? rhs : lhs;
    }

    public final int getHour() {
        return hour;
    }

    public boolean isBefore(NotificationTime other) {
        if (alarmDate.isBefore(other.alarmDate)) return true;
        else if (alarmDate.isAfter(other.alarmDate)) return false;
        else return hour < other.hour;
    }

    public LocalDate getAlarmDate() {
        return alarmDate;
    }

    public static boolean shouldBeNotified(IClock clock, IEvent event) {
        int days = Days.daysBetween(clock.now(), event.getDate()).getDays();
        if (days <= event.getNbrOfDaysForNotification()) {
            int hour = clock.hour();
            if (days == 0) return hour >= START_OF_DAY && hour < EVENING;
            return  hour >= EVENING;
        }
        return false;
    }
}
