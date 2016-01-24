package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;

import rx.Observable;

public interface AddBirthdayContract {
    String EXTRA_KEY_BIRTHDAY = "birthday";


    interface FragmentView {

        void showDate(String date);

        void enableSaveButton(Boolean enabled);

        void showSavedBirthday(String name);

        void showNothingSaved();
    }
    interface UserActionsListener {

        void restoreFromInstanceState(FragmentView fragmentView, Bundle savedInstanceState);

        void saveInstanceState(Bundle outState);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> lastNameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void clearInputObservables();

        void saveBirthday();
    }
}
