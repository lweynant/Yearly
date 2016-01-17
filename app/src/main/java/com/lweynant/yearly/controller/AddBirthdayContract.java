package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.model.Date;

public interface AddBirthdayContract {
    String EXTRA_KEY_BIRTHDAY = "birthday";

    interface View {

        void showDate(String date);
    }
    interface UserActionsListener {

        void restoreFromInstanceState(AddBirthdayContract.View view, Bundle savedInstanceState);

        void saveInstanceState(Bundle outState);

        void setName(String name);

        void setLastName(String name);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        Intent saveBirthday();
    }
}
