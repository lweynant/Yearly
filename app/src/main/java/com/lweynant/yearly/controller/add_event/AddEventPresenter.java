package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Date;

public class AddEventPresenter implements AddEventContract.UserActionListener{

    private AddEventContract.FragmentView fragmentView;
    private DateFormatter dateFormatter;

    public AddEventPresenter(DateFormatter dateFormatter){
        this.dateFormatter = dateFormatter;
    }

    @Override
    public void restoreFromInstanceState(AddEventContract.FragmentView fragmentView, Bundle savedInstanceState) {
        this.fragmentView = fragmentView;
    }

    @Override public void setDate(int year, @Date.Month int month, int day) {
        fragmentView.showDate(dateFormatter.format(year, month, day));
    }

    @Override public void setDate(@Date.Month int month, int day) {
        fragmentView.showDate(dateFormatter.format(month, day));
    }
}
