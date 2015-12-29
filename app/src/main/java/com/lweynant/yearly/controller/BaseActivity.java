package com.lweynant.yearly.controller;

import android.support.v7.app.AppCompatActivity;

import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.YearlyAppComponent;

public class BaseActivity extends AppCompatActivity {

    protected YearlyAppComponent getComponent() {
        return ((YearlyApp) getApplication()).getComponent();
    }
}
