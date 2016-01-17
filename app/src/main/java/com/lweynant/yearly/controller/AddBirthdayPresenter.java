package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;

import timber.log.Timber;

public class AddBirthdayPresenter implements AddBirthdayContract.UserActionsListener {
    private final DateFormatter dateFormatter;
    private AddBirthdayContract.View view;

    private BirthdayBuilder birthdayBuilder;
    private Bundle birthdayBundle;
    private Intent resultIntent;

    public AddBirthdayPresenter(BirthdayBuilder birthdayBuilder, DateFormatter dateFormatter, Bundle bundle, Intent resultIntent) {
        this.birthdayBuilder = birthdayBuilder;
        this.birthdayBundle = bundle;
        this.resultIntent = resultIntent;
        this.dateFormatter = dateFormatter;
    }
    @Override public void restoreFromInstanceState(AddBirthdayContract.View view,  Bundle savedInstanceState) {
        this.view = view;
        if (savedInstanceState != null) {
            this.birthdayBuilder.set(savedInstanceState);
        }
    }

    @Override public void setName(String name) {
        birthdayBuilder.setName(name.toString());
    }

    @Override public void setLastName(String name) {
        birthdayBuilder.setLastName(name.toString());
    }

    @Override public Intent saveBirthday() {
        Timber.d("saveBirthday");
        birthdayBuilder.archiveTo(birthdayBundle);
        resultIntent.putExtra(AddBirthdayContract.EXTRA_KEY_BIRTHDAY, birthdayBundle);
        return resultIntent;
    }

    @Override public void saveInstanceState(Bundle outState) {
        birthdayBuilder.archiveTo(outState);
    }

    @Override public void setDate(int year, @Date.Month int month, int day) {
        birthdayBuilder.setYear(year).setMonth(month).setDay(day);
        view.showDate(dateFormatter.format(year, month, day));
    }

    @Override public void setDate(@Date.Month int month, int day) {
        birthdayBuilder.clearYear().setMonth(month).setDay(day);
        view.showDate(dateFormatter.format(month, day));
    }

    private void handleSelection(int month, int day, String date) {
        birthdayBuilder.setMonth(month).setDay(day);
        view.showDate(date);
    }
}
