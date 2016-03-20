package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IKeyValueArchiver;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;

public class AddBirthdayPresenter implements AddBirthdayContract.UserActionsListener {
    private ITransaction transaction;
    private final DateFormatter dateFormatter;
    private IClock clock;
    private AddBirthdayContract.FragmentView fragmentView;

    private BirthdayBuilder birthdayBuilder;
    private final CompositeSubscription subcription = new CompositeSubscription();

    public AddBirthdayPresenter(BirthdayBuilder birthdayBuilder, ITransaction transaction,  DateFormatter dateFormatter, IClock clock) {
        this.birthdayBuilder = birthdayBuilder;
        this.transaction = transaction;
        this.dateFormatter = dateFormatter;
        this.clock = clock;
    }
    @SuppressWarnings("ResourceType")
    @Override public void initialize(AddBirthdayContract.FragmentView fragmentView,  Bundle args, Bundle savedInstanceState) {
        this.fragmentView = fragmentView;
        LocalDate now = clock.now();
        if (savedInstanceState != null) {
            birthdayBuilder.set(savedInstanceState);
            int selectedYear = readIntFromBundle(savedInstanceState, IEvent.KEY_YEAR, now.getYear());
            int selectedMonth = readIntFromBundle(savedInstanceState, IEvent.KEY_MONTH, now.getMonthOfYear());
            int selectedDay = readIntFromBundle(savedInstanceState, IEvent.KEY_DAY, now.getDayOfMonth());
            fragmentView.initialize(null, null, null, selectedYear, selectedMonth, selectedDay);
        }
        else{
            birthdayBuilder.set(args);
            if (birthdayBuilder.canBuild()) {
                Birthday event = birthdayBuilder.build();
                LocalDate date = event.getDate();
                String formattedDate;
                int year;
                if (event.hasYearOfOrigin()) {
                    year = event.getYearOfOrigin();
                    formattedDate = dateFormatter.format(year, date.getMonthOfYear(), date.getDayOfMonth());
                } else {
                    year = date.getYear();
                    formattedDate = dateFormatter.format(date.getMonthOfYear(), date.getDayOfMonth());
                }
                fragmentView.initialize(event.getName(), event.getLastName(), formattedDate, year, date.getMonthOfYear(), date.getDayOfMonth());
            }
            else {
                fragmentView.initialize(null, null, null, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
            }
        }
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
