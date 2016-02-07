package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEventRepoTransaction;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AddBirthdayPresenter implements AddBirthdayContract.UserActionsListener {
    private IEventRepoTransaction transaction;
    private final DateFormatter dateFormatter;
    private AddBirthdayContract.FragmentView fragmentView;

    private BirthdayBuilder birthdayBuilder;
    private final CompositeSubscription subcription = new CompositeSubscription();

    public AddBirthdayPresenter(BirthdayBuilder birthdayBuilder, IEventRepoTransaction transaction,  DateFormatter dateFormatter) {
        this.birthdayBuilder = birthdayBuilder;
        this.transaction = transaction;
        this.dateFormatter = dateFormatter;
    }
    @Override public void restoreFromInstanceState(AddBirthdayContract.FragmentView fragmentView,  Bundle savedInstanceState) {
        this.fragmentView = fragmentView;
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

    @Override
    public void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                    Observable<CharSequence> lastNameChangeEvents,
                                    Observable<CharSequence> dateChangeEvents) {
        Observable<Boolean> validName = nameChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> validDate = dateChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> enableSaveButton = Observable.combineLatest(validName, validDate, (a, b) -> a && b);

        subcription.add(nameChangeEvents
                .subscribe(n -> {
                    setName(n.toString());
                }));
        subcription.add(lastNameChangeEvents
                .subscribe(n -> {
                    setLastName(n.toString());
                }));

        subcription.add(enableSaveButton.distinctUntilChanged()
                .subscribe(enabled -> fragmentView.enableSaveButton(enabled)));

    }


    @Override public void saveBirthday() {
        //save to repo
        Birthday birthday = birthdayBuilder.build();
        if (birthday != null) {
            transaction.add(birthday).commit();
            fragmentView.showSavedBirthday(birthday.getName());
        }
        else {
            fragmentView.showNothingSaved();
        }
    }


    @Override public void saveInstanceState(Bundle outState) {
        birthdayBuilder.archiveTo(outState);
        subcription.unsubscribe();
    }

    @Override public void setDate(int year, @Date.Month int month, int day) {
        birthdayBuilder.setYear(year).setMonth(month).setDay(day);
        fragmentView.showDate(dateFormatter.format(year, month, day));
    }

    @Override public void setDate(@Date.Month int month, int day) {
        birthdayBuilder.clearYear().setMonth(month).setDay(day);
        fragmentView.showDate(dateFormatter.format(month, day));
    }

}
