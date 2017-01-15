package com.lweynant.yearly.platform;

import org.joda.time.LocalDate;

public class AlarmDecorator implements IAlarm {
    private IAlarm decoratee;

    public AlarmDecorator(IAlarm decoratee) {
        this.decoratee = decoratee;
    }
    @Override public void scheduleAlarm(LocalDate date, int hour) {
        decoratee.scheduleAlarm(date, hour);
    }

    @Override public void clear() {
        decoratee.clear();
    }
}
