package com.lweynant.yearly.platform;

import org.joda.time.LocalDate;

public interface IRawAlarm {
    void scheduleAlarm(LocalDate date, int hour);

    void clear();

}
