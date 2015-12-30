package com.lweynant.yearly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.NotificationTime;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import rx.Subscriber;
import timber.log.Timber;

public class AlarmGenerator extends Subscriber<NotificationTime> {
    private final PendingIntent alarmSender;
    private Context context;

    public AlarmGenerator(Context context) {
        this.context = context;
        Intent intent = new Intent(context, AlarmReceiver.class);

        this.alarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onCompleted() {
        Timber.d("onCompleted");
        this.context = null;

    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "onError");
        this.context = null;
    }

    @Override
    public void onNext(NotificationTime notificationTime) {
        Timber.d("onNext set alarm on %s at %d", notificationTime.getAlarmDate(), notificationTime.getHour());

        LocalDate alarmDate = notificationTime.getAlarmDate();
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime time = new DateTime(alarmDate.getYear(), alarmDate.getMonthOfYear(), alarmDate.getDayOfMonth(), notificationTime.getHour(), 0);
        long triggerAtMillis = time.toDateTime().getMillis();
        Timber.i("shedule an alarm on date: %s", time.toString());
        am.set(AlarmManager.RTC, triggerAtMillis, alarmSender);

    }


}
