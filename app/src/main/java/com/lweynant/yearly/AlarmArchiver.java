package com.lweynant.yearly;

import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.IRawAlarm;

import org.joda.time.LocalDate;

import timber.log.Timber;

public class AlarmArchiver extends AlarmDecorator {
    public static final String CURRENT_ALARM = "com.lweynant.current_alarm";
    private final IPreferences preferences;
    private final IDateFormatter dateFormatter;

    public AlarmArchiver(IRawAlarm alarm, IPreferences preferences, IDateFormatter dateFormatter) {
        super(alarm);
        this.preferences = preferences;
        this.dateFormatter = dateFormatter;
    }

    @Override public void scheduleAlarm(LocalDate date, int hour) {
        Timber.d("sheduleAlarm");
        super.scheduleAlarm(date, hour);
        String time = dateFormatter.format(date, hour);
        preferences.setStringValue(CURRENT_ALARM, time);
    }

    @Override public void clear() {
        Timber.d("clear");
        super.clear();
        preferences.remove(CURRENT_ALARM);
    }
}
