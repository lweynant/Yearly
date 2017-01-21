package com.lweynant.yearly;

import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IRawAlarm;

import org.joda.time.LocalDate;

public class AlarmDecorator implements IAlarm {
    private IRawAlarm decoratee;

    public AlarmDecorator(IRawAlarm decoratee) {
        this.decoratee = decoratee;
    }
    @Override public void scheduleAlarm(LocalDate date, int hour) {
        decoratee.scheduleAlarm(date, hour);
    }

    @Override public void clear() {
        decoratee.clear();
    }
}
