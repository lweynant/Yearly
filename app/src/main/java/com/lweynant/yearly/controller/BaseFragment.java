package com.lweynant.yearly.controller;

import android.support.v4.app.Fragment;

import com.lweynant.yearly.YearlyApp;
import com.squareup.leakcanary.RefWatcher;

public class BaseFragment extends Fragment {
    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = YearlyApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
