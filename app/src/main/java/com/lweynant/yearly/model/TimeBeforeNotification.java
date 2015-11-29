package com.lweynant.yearly.model;

public class TimeBeforeNotification {
    private final int days;
    private final int hour;

    public TimeBeforeNotification(final int days, final int hour) {
        this.days = days;
        this.hour = hour;
    }

    public final int getDays() {
        return days;
    }

    public final int getHour() {
        return hour;
    }

    public boolean isBefore(TimeBeforeNotification other) {
        if (days < other.days) return true;
        else if (days == other.days) return hour < other.hour;
        return false;
    }

    public static TimeBeforeNotification min(TimeBeforeNotification rhs, TimeBeforeNotification lhs) {
        return rhs.isBefore(lhs) ? rhs : lhs;
    }
}
