package com.lweynant.yearly.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.controller.list_events.ListBirthdaysActivity;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, ListBirthdaysActivity.class);
        startActivity(intent);
        finish();
    }
}
