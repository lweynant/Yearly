package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.YearlyApp;

import timber.log.Timber;

abstract public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate - injecting dependencies");
        injectDependencies(getComponent());
        super.onCreate(savedInstanceState);
    }

    protected abstract void injectDependencies(BaseYearlyAppComponent component);

    private BaseYearlyAppComponent getComponent() {
        return ((YearlyApp) getApplication()).getComponent();
    }
}
