package com.lweynant.yearly.controller;

import android.content.Intent;
import android.os.Bundle;

import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class AddBirthdayPresenter implements AddBirthdayContract.UserActionsListener {
    private final DateFormatter dateFormatter;
    private AddBirthdayContract.View view;

    private BirthdayBuilder birthdayBuilder;
    private CompositeSubscription subcription;

    public AddBirthdayPresenter(BirthdayBuilder birthdayBuilder, DateFormatter dateFormatter) {
        this.birthdayBuilder = birthdayBuilder;
        this.dateFormatter = dateFormatter;
    }
    @Override public void restoreFromInstanceState(AddBirthdayContract.View view,  Bundle savedInstanceState) {
        this.view = view;
        if (savedInstanceState != null) {
            birthdayBuilder.set(savedInstanceState);
        }
    }

    private void setName(String name) {
        birthdayBuilder.setName(name.toString());
    }

    private void setLastName(String name) {
        birthdayBuilder.setLastName(name.toString());
    }

    @Override public void archiveBirthdayTo(Bundle bundle) {
        Timber.d("archiveBirthdayTo");
        birthdayBuilder.archiveTo(bundle);
    }

    @Override
    public void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                    Observable<CharSequence> lastNameChangeEvents,
                                    Observable<CharSequence> dateChangeEvents) {
        subcription = new CompositeSubscription();
        Observable<Boolean> validName = nameChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> validDate = dateChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> enableSaveButton = Observable.combineLatest(validName, validDate, (a, b) -> a && b)
                .doOnNext(b -> System.out.print(b));

        subcription.add(nameChangeEvents
                .subscribe(n -> {
                    setName(n.toString());
                }));
        subcription.add(lastNameChangeEvents
                .subscribe(n -> {
                    setLastName(n.toString());
                }));

        subcription.add(enableSaveButton.distinctUntilChanged()
                .subscribe(enabled -> view.enableSaveButton(enabled)));

    }

    @Override public void clearInputObservables() {
        subcription.unsubscribe();
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

}
