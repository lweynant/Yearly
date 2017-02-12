package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.BootReceiver;
import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AddBirthdayPresenter implements AddBirthdayContract.UserActionsListener {
    private ITransaction transaction;
    private final IDateFormatter dateFormatter;
    private IClock clock;
    private AddBirthdayContract.FragmentView fragmentView;

    private BirthdayBuilder birthdayBuilder;
    private final CompositeSubscription subcription = new CompositeSubscription();
    private Bundle originalArgs;

    public AddBirthdayPresenter(BirthdayBuilder birthdayBuilder, ITransaction transaction, IDateFormatter dateFormatter, IClock clock) {
        this.birthdayBuilder = birthdayBuilder;
        this.transaction = transaction;
        this.dateFormatter = dateFormatter;
        this.clock = clock;
    }
    @SuppressWarnings("ResourceType")
    @Override public void initialize(AddBirthdayContract.FragmentView fragmentView, Bundle args) {
        this.fragmentView = fragmentView;
        LocalDate now = clock.now();
        birthdayBuilder.set(args);
        originalArgs = args;
        int selectedYear = readIntFromBundle(args, IEvent.KEY_YEAR, now.getYear());
        int selectedMonth = readIntFromBundle(args, IEvent.KEY_MONTH, now.getMonthOfYear());
        int selectedDay = readIntFromBundle(args, IEvent.KEY_DAY, now.getDayOfMonth());
        String firstName = readStringFromBundle(args, IEvent.KEY_NAME, null);
        String lastName = readStringFromBundle(args, Birthday.KEY_LAST_NAME, null);
        String formattedDate = null;
        if (args.containsKey(IEvent.KEY_DAY) && args.containsKey(IEvent.KEY_MONTH)) {
            if (args.containsKey(IEvent.KEY_YEAR)) {
                formattedDate = dateFormatter.format(selectedYear, selectedMonth, selectedDay);
            } else {
                formattedDate = dateFormatter.format(selectedMonth, selectedDay);
            }
        }
        fragmentView.initialize(firstName, lastName, formattedDate, selectedYear, selectedMonth, selectedDay);
    }

    private String readStringFromBundle(Bundle bundle, String key, String defaultValue) {
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        }
        return defaultValue;
    }

    private int readIntFromBundle(Bundle bundle, String key, int defaultValue) {
        if (bundle.containsKey(key)) {
            return bundle.getInt(key);
        }
        return defaultValue;
    }


    private void setName(String name) {
        birthdayBuilder.setName(name.toString());
    }

    private void setLastName(String name) {
        birthdayBuilder.setLastName(name.toString());
    }

    @Override
    public void setInputObservables(Observable<CharSequence> firstNameChangeEvents,
                                    Observable<CharSequence> lastNameChangeEvents,
                                    Observable<CharSequence> dateChangeEvents) {
        Observable<Boolean> validFirstName = firstNameChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);
        Observable<Boolean> validLastName = lastNameChangeEvents.map(t -> true);




        Observable<Boolean> validDate = dateChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);

        Observable<Boolean> validName = Observable.combineLatest(validFirstName, validLastName, (f, l) -> f && l);
        Observable<Boolean> enableSaveButton = Observable.combineLatest(validName, validDate, (a, b) -> a && b);

        subcription.add(firstNameChangeEvents
                .subscribe(n -> {
                    setName(n.toString());
                }));
        subcription.add(lastNameChangeEvents
                .subscribe(n -> {
                    setLastName(n.toString());
                }));

        subcription.add(enableSaveButton.distinctUntilChanged()
                //.doOnNext(b -> System.out.print(b))
                .subscribe(enabled -> fragmentView.enableSaveButton(enabled)));

    }


    @Override public void saveBirthday() {
        //save to repo
        Birthday birthday = birthdayBuilder.build();
        if (birthday != null) {
            transaction.add(birthday).commit();
        }
        showBirthday(birthday);
    }

    @Override public boolean isBirthdaySaved() {
        return !fragmentView.isSaveButtonEnabled();
    }

    @Override public void throwAwayModifications() {
        birthdayBuilder.set(originalArgs);
        showBirthday(birthdayBuilder.build());
    }

    private void showBirthday(Birthday birthday) {
        if (birthday != null) {
            fragmentView.showSavedBirthday(birthday);
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
