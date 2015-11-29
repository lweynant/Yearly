package com.lweynant.yearly;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.util.IClock;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import rx.Subscriber;
import timber.log.Timber;

public class AlarmGenerator extends Subscriber<Integer> {
    private final PendingIntent alarmSender;
    private final LocalDate from;
    private Context context;

    AlarmGenerator(Context context, LocalDate from)
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
    public void onNext(Integer event) {
        Timber.d("onNext set alarm in  %d days", event);
        int morning = 6;
        int evening = 19;
        int hour = event == 0? morning : evening;
        LocalDate alarmDate = from.plusDays(event);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        DateTime time = new DateTime(alarmDate.getYear(), alarmDate.getMonthOfYear(), alarmDate.getDayOfMonth(), hour, 0);
        long triggerAtMillis = time.toDateTime().getMillis();
        Timber.d("shedule an alarm on date: %s",  time.toString());
        am.set(AlarmManager.RTC, triggerAtMillis, alarmSender);

    }
}
