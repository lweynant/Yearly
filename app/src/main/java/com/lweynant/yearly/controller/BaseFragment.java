package com.lweynant.yearly.controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.YearlyApp;

import timber.log.Timber;

public abstract class BaseFragment extends Fragment implements IExtendeFragmentLifeCycle{

    private BaseYearlyAppComponent getComponent() {
        return ((YearlyApp) getActivity().getApplication()).getComponent();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate - injecting dependencies");
        super.onCreate(savedInstanceState);
        injectDependencies(getComponent());
    }

    abstract protected void injectDependencies(BaseYearlyAppComponent component);

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override public boolean onBackPressed() {
        return false;
    }

    @Override public boolean onOptionsItemHomePressed() {
        return false;
    }
}
