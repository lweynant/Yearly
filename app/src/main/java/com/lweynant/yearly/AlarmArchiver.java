package com.lweynant.yearly;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.IRawAlarm;

import org.joda.time.LocalDate;

import timber.log.Timber;

public class AlarmArchiver extends AlarmDecorator {
    public static final String CURRENT_ALARM = "com.lweynant.current_alarm";
    public static final String ALARM_SET_AT = "com.lweynant.alarm_set_at";
    private final IPreferences preferences;
    private final IDateFormatter dateFormatter;
    private IClock clock;

    public AlarmArchiver(IRawAlarm alarm, IPreferences preferences,
                         IDateFormatter dateFormatter,
                         IClock clock) {
        super(alarm);
        this.preferences = preferences;
        this.dateFormatter = dateFormatter;
        this.clock = clock;
    }

    @Override public void scheduleAlarm(LocalDate date, int hour) {
        Timber.d("sheduleAlarm");
        super.scheduleAlarm(date, hour);
        preferences.setStringValue(CURRENT_ALARM, dateFormatter.format(date, hour));
        String time = dateFormatter.format(clock);
        preferences.setStringValue(ALARM_SET_AT, time);
    }

    @Override public void clear() {
        Timber.d("clear");
        super.clear();
        preferences.remove(CURRENT_ALARM);
        preferences.remove(ALARM_SET_AT);
    }
}
