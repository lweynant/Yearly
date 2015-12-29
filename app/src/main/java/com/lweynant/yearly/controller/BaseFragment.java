package com.lweynant.yearly.controller;

import android.support.v4.app.Fragment;

import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.YearlyAppComponent;

public class BaseFragment extends Fragment {

    protected YearlyAppComponent getComponent() {
        return ((YearlyApp) getActivity().getApplication()).getComponent();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
