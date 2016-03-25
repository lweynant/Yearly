package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public interface AddBirthdayContract {

    interface FragmentView {

        void initialize(String name, String lastName, String formattedDate, int selectedYear, @Date.Month int selectedMonth, int selectedDay);

        void showDate(String date);

        void enableSaveButton(Boolean enabled);

        void showSavedBirthday(IEvent event);

        void showNothingSaved();
    }
    interface UserActionsListener {

        void initialize(FragmentView fragmentView, Bundle args);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> lastNameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        void saveInstanceState(Bundle outState);

        void saveBirthday();
    }
}
