package com.lweynant.yearly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.TimeBeforeNotification;

import org.joda.time.LocalDate;

import rx.Observable;
import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Timber.d("generating the alarm...");
            YearlyApp app = (YearlyApp) context.getApplicationContext();

            Observable<TimeBeforeNotification> nextAlarmObservable = app.getRepo().timeBeforeFirstUpcomingEvent(LocalDate.now());
            nextAlarmObservable.subscribe(new AlarmGenerator(context, LocalDate.now()));


        }
    }
}
