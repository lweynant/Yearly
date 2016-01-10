package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.AlarmGenerator;
import com.lweynant.yearly.platform.IAlarm;

import rx.Observable;
import timber.log.Timber;

public class SyncWithTestsAlarmGenerator extends AlarmGenerator {
    private final CountingIdlingResource idlingResource;

    public SyncWithTestsAlarmGenerator(CountingIdlingResource idlingResource, IAlarm alarm) {
        super(alarm);
        this.idlingResource = idlingResource;
    }

    @Override public void generate(Observable<NotificationTime> nextAlarmObservable) {
        Timber.d("generate - increment idlingResource");
        idlingResource.increment();
        super.generate(nextAlarmObservable);
    }

    @Override protected void onCompleted() {
        Timber.d("onCompleted - decrement idlingResource");
        super.onCompleted();
        idlingResource.decrement();
    }

    @Override protected void onError(Throwable e) {
        Timber.d("onError - decrement idlingResource");
        super.onError(e);
        idlingResource.decrement();
    }
}
