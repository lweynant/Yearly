package com.lweynant.yearly.platform;

import org.joda.time.LocalDate;

public interface IAlarm {
    void scheduleAlarm(LocalDate date, int hour);

    void clear();
}
