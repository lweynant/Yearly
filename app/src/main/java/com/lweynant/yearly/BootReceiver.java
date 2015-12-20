package com.lweynant.yearly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.NotificationTime;

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
            EventRepo repo = ((YearlyApp) context.getApplicationContext()).getRepo();

            Observable<NotificationTime> nextAlarmObservable = repo.notificationTimeForFirstUpcomingEvent(LocalDate.now());
            nextAlarmObservable.subscribe(new AlarmGenerator(context));


        }
    }
}
