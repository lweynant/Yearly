package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.platform.IAlarm;

import org.joda.time.LocalDate;

import rx.Observable;
import timber.log.Timber;

public class SyncWithTestsAlarmGenerator extends AlarmGenerator {
    private final CountingIdlingResource idlingResource;

    public SyncWithTestsAlarmGenerator(CountingIdlingResource idlingResource, IAlarm alarm) {
        super(alarm);
        this.idlingResource = idlingResource;
    }

    @Override public void generate(Observable<IEvent> events, LocalDate now) {
        Timber.d("generate - increment idlingResource");
        idlingResource.increment();
        super.generate(events, now);
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
