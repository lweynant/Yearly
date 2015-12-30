package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.YearlyAppComponent;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment {

    private YearlyAppComponent getComponent() {
        return ((YearlyApp) getActivity().getApplication()).getComponent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate - resolving dependencies");
        super.onCreate(savedInstanceState);
        resolveDependencies(getComponent());
    }

    abstract protected void resolveDependencies(YearlyAppComponent component);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
