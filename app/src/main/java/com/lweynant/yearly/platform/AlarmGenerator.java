package com.lweynant.yearly.platform;


import com.lweynant.yearly.model.NotificationTime;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class AlarmGenerator  {

    private IAlarm alarm;

    private class AlarmSubscriber extends Subscriber<NotificationTime> {


        boolean scheduledAlarm = false;
        AlarmSubscriber() {

        }
        @Override public void onCompleted() {
            Timber.d("onCompleted");
            AlarmGenerator.this.onCompleted();
            if (!scheduledAlarm) alarm.clear();
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
            scheduledAlarm = true;
        }
    }

    public AlarmGenerator(IAlarm alarm) {
        Timber.d("create AlarmGenerator instance");
        this.alarm = alarm;
    }

    public void generate(Observable<NotificationTime> nextAlarmObservable) {
        nextAlarmObservable.subscribeOn(Schedulers.io())
                .subscribe(new AlarmSubscriber());

    }

    protected void onCompleted() {
        Timber.d("onCompleted");
    }

    protected void onError(Throwable e) {
        Timber.d(e, "onError");
    }

}
