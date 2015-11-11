package com.lweynant.yearly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.joda.time.LocalDate;

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

            AlarmGenerator alarmGenerator = new AlarmGenerator(context, app.getRepo());
            alarmGenerator.startAlarm(LocalDate.now());

        }
    }
}
