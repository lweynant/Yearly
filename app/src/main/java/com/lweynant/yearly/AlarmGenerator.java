package com.lweynant.yearly;


import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IAlarm;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import rx.Subscriber;
import timber.log.Timber;

public class AlarmGenerator extends Subscriber<NotificationTime> {

    private IAlarm alarm;

    public AlarmGenerator(IAlarm alarm) {
        this.alarm = alarm;
    }

    @Override
    public void onCompleted() {
        Timber.d("onCompleted");
        this.alarm = null;

    }

    @Override
    public void onError(Throwable e) {
        Timber.e(e, "onError");
        this.alarm = null;
    }

    @Override
    public void onNext(NotificationTime notificationTime) {
        Timber.d("onNext set alarm on %s at %d", notificationTime.getAlarmDate(), notificationTime.getHour());

        alarm.scheduleAlarm(notificationTime.getAlarmDate(), notificationTime.getHour());
    }


}
