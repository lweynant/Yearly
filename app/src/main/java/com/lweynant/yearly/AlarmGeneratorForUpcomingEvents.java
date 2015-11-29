package com.lweynant.yearly;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public class AlarmGeneratorForUpcomingEvents {
    private final EventRepo repo;
    private Context context;
    private PendingIntent mAlarmSender;

    public AlarmGeneratorForUpcomingEvents(Context context, EventRepo repo) {
        this.context = context;
        this.repo = repo;
        Intent intent = new Intent(context, AlarmReceiver.class);
        mAlarmSender = PendingIntent.getBroadcast(context, 0, intent, 0);
    }


    public void startAlarm(final LocalDate from) {
        Timber.d("startAlarm");

        Subscription subscription;
        subscription = repo.getEvents()
                .map(new Func1<IEvent, Integer>() {
                    @Override
                    public Integer call(IEvent event) {
                        int days = Days.daysBetween(from, event.getDate()).getDays();
                        if (days > 0)
                        {
                            days = days - 1;
                            days = days < 0? 0:days;
                        }
                        return days;
                    }
                })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 0? false:true;
                    }
                })
                .reduce(Integer.MAX_VALUE, new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer currentMin, Integer number) {
                        return currentMin < number ? currentMin : number;
                    }
                })
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer == Integer.MAX_VALUE?  false:  true;
                    }
                })
                .subscribe(new AlarmGenerator(context, from));
        subscription.unsubscribe();
    }

}
