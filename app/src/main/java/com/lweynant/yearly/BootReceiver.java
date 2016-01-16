package com.lweynant.yearly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.platform.IClock;

import javax.inject.Inject;

import timber.log.Timber;

public class BootReceiver extends BroadcastReceiver {
    @Inject EventRepo repo;
    @Inject IClock clock;
    @Inject AlarmGenerator alarmGenerator;

    public BootReceiver() {
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Timber.d("onReceive");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Timber.d("generating the alarm...");
            ((YearlyApp) context.getApplicationContext()).getComponent().inject(this);

            alarmGenerator.generate(repo.getEventsSubscribedOnProperScheduler(), clock.now());
        }
    }
}
