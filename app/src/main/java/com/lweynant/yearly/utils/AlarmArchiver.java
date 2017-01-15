package com.lweynant.yearly.utils;

import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.ITimeConvertor;

import org.joda.time.LocalDate;

import timber.log.Timber;

public class AlarmArchiver extends AlarmDecorator {
    public static final String CURRENT_ALARM = "com.lweynant.current_alarm";
    private final IPreferences preferences;
    private ITimeConvertor timeConvertor;

    public AlarmArchiver(IAlarm alarm, IPreferences preferences, ITimeConvertor timeConvertor) {
        super(alarm);
        this.preferences = preferences;
        this.timeConvertor = timeConvertor;
    }

    @Override public void scheduleAlarm(LocalDate date, int hour) {
        Timber.d("sheduleAlarm");
        super.scheduleAlarm(date, hour);
        String time = timeConvertor.convert(date, hour);
        preferences.setStringValue(CURRENT_ALARM, time);
    }

    @Override public void clear() {
        Timber.d("clear");
        super.clear();
        preferences.remove(CURRENT_ALARM);
    }
}
