package com.lweynant.yearly.platform;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import timber.log.Timber;

public class Clock implements IClock {
    public Clock() {
        Timber.d("create the clock");
    }

    @Override public LocalDate now() {
        return LocalDate.now();
    }

    @Override public int hour() {
        return DateTime.now().getHourOfDay();
    }

    @Override public int minutes() {
        return DateTime.now().getMinuteOfHour();
    }

    @Override public String timestamp() {
        return DateTime.now().toString();
    }

    @Override public int seconds() {
        return DateTime.now().getSecondOfMinute();
    }
}
