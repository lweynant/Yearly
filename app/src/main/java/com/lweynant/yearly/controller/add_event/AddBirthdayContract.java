package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;

import rx.Observable;

public interface AddBirthdayContract {
    String EXTRA_KEY_BIRTHDAY = AddBirthdayContract.class.getCanonicalName();

    interface FragmentView {

        void initialize(String name, String lastName, String formattedDate, int selectedYear, @Date.Month int selectedMonth, int selectedDay);

        void showDate(String date);

        void enableSaveButton(Boolean enabled);

        void showSavedBirthday(String name);

        void showNothingSaved();
    }
    interface UserActionsListener {

        void initialize(FragmentView fragmentView, Bundle args, Bundle savedInstanceState);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> lastNameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        void saveInstanceState(Bundle outState);

        void saveBirthday();
    }
}
