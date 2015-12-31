package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.BaseYearlyAppComponent;

import timber.log.Timber;

abstract public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate - injecting dependencies");
        super.onCreate(savedInstanceState);
        injectDependencies(getComponent());
    }

    protected abstract void injectDependencies(BaseYearlyAppComponent component);

    private BaseYearlyAppComponent getComponent() {
        return ((YearlyApp) getApplication()).getComponent();
    }
}
