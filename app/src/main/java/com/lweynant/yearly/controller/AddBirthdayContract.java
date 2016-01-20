package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;

import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.model.Date;

import rx.Observable;

public interface AddBirthdayContract {
    String EXTRA_KEY_BIRTHDAY = "birthday";

    interface View {

        void showDate(String date);

        void enableSaveButton(Boolean enabled);
    }
    interface UserActionsListener {

        void restoreFromInstanceState(AddBirthdayContract.View view, Bundle savedInstanceState);

        void saveInstanceState(Bundle outState);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        void archiveBirthdayTo(Bundle bundle);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> lastNameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void clearInputObservables();
    }
}
