package com.lweynant.yearly.controller;


import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IAlarm;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class AlarmGenerator  {

    private final IAlarm alarm;
    private Subscription subscription;
    public AlarmGenerator(IAlarm alarm) {
        Timber.d("create AlarmGenerator instance");
        this.alarm = alarm;
    }
    public void generate(Observable<IEvent> events, LocalDate now) {
        Timber.d("generate");
        unsubscribeFromPreviousSubscription();
        subscription = events
                .map(event -> new NotificationTime(now, event))
                .reduce((currentMin, x) -> NotificationTime.min(currentMin, x))
                .subscribe(t -> alarm.scheduleAlarm(t.getAlarmDate(), t.getHour()),
                           e -> { alarm.clear(); onError(e); },
                           () -> onCompleted());
    }


    protected void onCompleted() {
        Timber.d("onCompleted");
    }

    protected void onError(Throwable e) {
        Timber.d(e, "onError");
    }

    private void unsubscribeFromPreviousSubscription() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            Timber.d("unsubscribe from previous transcription");
            subscription.unsubscribe();
        }
    }
}
