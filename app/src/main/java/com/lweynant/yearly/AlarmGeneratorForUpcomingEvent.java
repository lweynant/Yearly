package com.lweynant.yearly;

import android.content.Context;

import com.lweynant.yearly.model.TimeBeforeNotification;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;

import rx.Subscription;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public class AlarmGeneratorForUpcomingEvent {
    private final EventRepo repo;
    private Context context;

    public AlarmGeneratorForUpcomingEvent(Context context, EventRepo repo) {
        this.context = context;
        this.repo = repo;
    }


    public void startAlarm(final LocalDate from) {
        Timber.d("startAlarm");

        Subscription subscription;
        subscription = repo.getEvents()
                .map(event -> Event.timeBeforeNotification(from, event))
                .reduce((currentMin, x) -> TimeBeforeNotification.min(currentMin, x))
                .subscribe(new AlarmGenerator(context, from));
        subscription.unsubscribe();
    }

}
