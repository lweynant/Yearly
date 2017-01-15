package com.lweynant.yearly.platform;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import timber.log.Timber;

public class AlarmArchiver extends AlarmDecorator {
    public static final String CURRENT_ALARM = "com.lweynant.current_alarm";
    private Context context;

    public AlarmArchiver(IAlarm alarm, Context context) {
        super(alarm);
        this.context = context;
    }

    @Override public void scheduleAlarm(LocalDate date, int hour) {
        Timber.d("sheduleAlarm");
        super.scheduleAlarm(date, hour);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        DateTime time = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), hour, 0);

        prefs.edit().putString(CURRENT_ALARM, time.toString()).commit();
    }

    @Override public void clear() {
        Timber.d("clear");
        super.clear();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().remove(CURRENT_ALARM).commit();
    }
}
