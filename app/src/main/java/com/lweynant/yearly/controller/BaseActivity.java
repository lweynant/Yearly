package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.YearlyAppComponent;

import timber.log.Timber;

abstract public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate - resolving dependencies");
        super.onCreate(savedInstanceState);
        resolveDependencies(getComponent());
    }

    protected abstract void resolveDependencies(YearlyAppComponent component);

    private YearlyAppComponent getComponent() {
        return ((YearlyApp) getApplication()).getComponent();
    }
}
