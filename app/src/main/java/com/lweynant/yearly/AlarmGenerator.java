package com.lweynant.yearly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.TimeBeforeNotification;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class AlarmGenerator extends Subscriber<TimeBeforeNotification> {
    private final PendingIntent alarmSender;
    private final LocalDate from;
    private Context context;

    public AlarmGenerator(Context context, LocalDate from)
    {
        this.context = context;
        this.from = from;
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
    public void onNext(TimeBeforeNotification days) {
        Timber.d("onNext set alarm in  %d days", days.getDays());
        if (days.getDays() < 0)
        {
            Timber.d("can not set alarms in the past");
            return;
        }
        LocalDate alarmDate = from.plusDays(days.getDays());
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime time = new DateTime(alarmDate.getYear(), alarmDate.getMonthOfYear(), alarmDate.getDayOfMonth(), days.getHour(), 0);
        long triggerAtMillis = time.toDateTime().getMillis();
        Timber.d("shedule an alarm on date: %s", time.toString());
        am.set(AlarmManager.RTC, triggerAtMillis, alarmSender);

    }


}
