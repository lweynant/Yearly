package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.LocalDate;

import rx.Observable;

public interface AddEventContract {

    interface FragmentView {

        void initialize(String name, String formattedDate, int selectedYear, @Date.Month int selectedMonth, int selectedDay);

        void showDate(String formattedDate);

        void showSavedEvent(IEvent event);

        void showNothingSaved();

        void enableSaveButton(Boolean enabled);
    }
    interface UserActionListener {

        void initialize(FragmentView fragmentView, Bundle args, Bundle savedInstanceState);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        void saveInstanceState(Bundle outState);

        void saveEvent();
    }
}
