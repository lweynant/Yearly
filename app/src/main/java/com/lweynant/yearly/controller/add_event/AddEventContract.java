package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;

public interface AddEventContract {
    interface FragmentView {
        void showDate(String date);
    }
    interface UserActionListener {
        void restoreFromInstanceState(FragmentView fragmentView, Bundle savedInstanceState);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

    }
}
