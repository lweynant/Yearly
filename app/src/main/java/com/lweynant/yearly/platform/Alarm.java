package com.lweynant.yearly.platform;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.AlarmReceiver;
import com.lweynant.yearly.model.NotificationTime;

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

    public void scheduleAlarm(NotificationTime notificationTime) {
        LocalDate alarmDate = notificationTime.getAlarmDate();
        DateTime time = new DateTime(alarmDate.getYear(), alarmDate.getMonthOfYear(), alarmDate.getDayOfMonth(), notificationTime.getHour(), 0);
        long triggerAtMillis = time.toDateTime().getMillis();

        Timber.i("shedule an alarm on date: %s", time.toString());
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, triggerAtMillis, alarmSender);
    }
    @Override public void scheduleAlarm(LocalDate date, int hour) {
        DateTime time = new DateTime(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), hour, 0);
        long triggerAtMillis = time.toDateTime().getMillis();

        Timber.i("shedule an alarm on date: %s", time.toString());
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC, triggerAtMillis, alarmSender);
    }

}
