package com.lweynant.yearly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.NotificationTime;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {
    @Inject EventRepo repo;
    public BootReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Timber.d("generating the alarm...");
            ((YearlyApp) context.getApplicationContext()).getComponent().inject(this);

            Observable<NotificationTime> nextAlarmObservable = repo.notificationTimeForFirstUpcomingEvent(LocalDate.now());
            nextAlarmObservable.subscribe(new AlarmGenerator(context));


        }
    }
}
