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
                .map(new Func1<IEvent, TimeBeforeNotification>() {
                    @Override
                    public TimeBeforeNotification call(IEvent event) {
                        return Event.timeBeforeNotification(from, event);
                    }
                })
                .reduce(new Func2<TimeBeforeNotification, TimeBeforeNotification, TimeBeforeNotification>() {
                    @Override
                    public TimeBeforeNotification call(TimeBeforeNotification currentMin, TimeBeforeNotification number) {
                        return TimeBeforeNotification.min(currentMin, number);
                    }
                })
                .subscribe(new AlarmGenerator(context, from));
        subscription.unsubscribe();
    }

}
