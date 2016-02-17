package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public interface AddEventContract {
    String EXTRA_KEY_EVENT = AddEventContract.class.getCanonicalName();

    interface FragmentView {

        void showDate(String date);

        void showSavedEvent(String name);

        void showNothingSaved();

        void enableSaveButton(Boolean enabled);
    }
    interface UserActionListener {

        void restoreFromSavedInstanceState(FragmentView fragmentView, Bundle savedInstanceState);

        void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                 Observable<CharSequence> dateChangeEvents);

        void setDate(int year, @Date.Month int month, int day);

        void setDate(@Date.Month int month, int day);

        void saveInstanceState(Bundle outState);

        void saveEvent();
    }
}
