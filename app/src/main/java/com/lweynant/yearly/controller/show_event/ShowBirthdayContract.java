package com.lweynant.yearly.controller.show_event;

import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.model.IEvent;


public interface ShowBirthdayContract {
    interface FragmentView {
        void showFirstName(String name);

        void showDate(String date);

        void showAge(int age);

        void showUnknownAge();

        void showNextEventIn(int totaldays, int months, int weeks, int days);

        void showNameOfDay(String day);

        void showEditUI(IEvent intent);

    }
    interface UserActionsListener {
        void initialize(FragmentView fragmentView, Bundle args);

        void editBirthday();

        void removeBirthday();

        String getTextToShare();
    }
}
