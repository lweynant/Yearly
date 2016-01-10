package com.lweynant.yearly.platform;


import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.NotificationTime;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.Subscriber;
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
        unsubscrbeFromPreviousSubscription();
        subscription = events
                .map(event -> new NotificationTime(now, event))
                .reduce((currentMin, x) -> NotificationTime.min(currentMin, x))
                .subscribe(new AlarmSubscriber());
    }


    protected void onCompleted() {
        Timber.d("onCompleted");
    }

    protected void onError(Throwable e) {
        Timber.d(e, "onError");
    }

    private void unsubscrbeFromPreviousSubscription() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            Timber.d("unsubscribe from previous transcription");
            subscription.unsubscribe();
        }
    }

    private class AlarmSubscriber extends Subscriber<NotificationTime> {


        AlarmSubscriber() {

        }
        @Override public void onCompleted() {
            Timber.d("onCompleted");
            AlarmGenerator.this.onCompleted();
        }

        @Override public void onError(Throwable e) {
            Timber.d("onError %s", e.toString());
            AlarmGenerator.this.onError(e);
            alarm.clear();
        }

        @Override public void onNext(NotificationTime notificationTime) {
            Timber.d("onNext");
            Timber.d("onNext set alarm on %s at %d", notificationTime.getAlarmDate(), notificationTime.getHour());

            alarm.scheduleAlarm(notificationTime.getAlarmDate(), notificationTime.getHour());
        }
    }


}
