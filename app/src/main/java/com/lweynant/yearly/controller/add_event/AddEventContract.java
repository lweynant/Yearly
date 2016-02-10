package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public interface AddEventContract {
    String EXTRA_KEY_EVENT = "event";

    interface FragmentView {
        void showDate(String date);

        void showSavedEvent(String name);

        void showNothingSaved();

        void enableSaveButton(Boolean enabled);
    }
    interface UserActionListener {
        void restoreFromInstanceState(FragmentView fragmentView, Bundle savedInstanceState);

        void setDate(int year, @Date.Month int month, int day);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void setDate(@Date.Month int month, int day);

        void saveEvent();

        void saveInstanceState(Bundle outState);
    }
}
