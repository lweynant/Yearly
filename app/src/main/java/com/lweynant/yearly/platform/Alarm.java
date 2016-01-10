package com.lweynant.yearly.platform;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.AlarmReceiver;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import timber.log.Timber;

public class Alarm  implements IAlarm {
    private final PendingIntent alarmSender;
    private Context context;

    public Alarm(Context context) {
        this.context = context;
        Intent intent = new Intent(context, AlarmReceiver.class);
        this.alarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override public void scheduleAlarm(LocalDate date, int hour) {
        DateTime time = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), hour, 0);
        long triggerAtMillis = time.toDateTime().getMillis();

        Timber.i("shedule an alarm on date: %s", time.toString());
        AlarmManager am = getAlarmManager();
        am.set(AlarmManager.RTC, triggerAtMillis, alarmSender);
    }

    private AlarmManager getAlarmManager() {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    @Override public void clear() {
        Timber.i("cancel the alarm");
        try {
            getAlarmManager().cancel(alarmSender);
        } catch (Exception e) {
            Timber.d("alarm was not cancelled");
        }
    }

}
