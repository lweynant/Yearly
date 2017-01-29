package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IPictureRepo;

import org.joda.time.LocalDate;

import java.io.File;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class AddBirthdayPresenter implements AddBirthdayContract.UserActionsListener {
    private ITransaction transaction;
    private IPictureRepo pictureRepo;
    private final IDateFormatter dateFormatter;
    private IClock clock;
    private AddBirthdayContract.FragmentView fragmentView;

    private BirthdayBuilder birthdayBuilder;
    private final CompositeSubscription subcription = new CompositeSubscription();
    private File picture;

    public AddBirthdayPresenter(BirthdayBuilder birthdayBuilder, ITransaction transaction,
                                IPictureRepo pictureRepo,
                                IDateFormatter dateFormatter, IClock clock) {
        this.birthdayBuilder = birthdayBuilder;
        this.transaction = transaction;
        this.pictureRepo = pictureRepo;
        this.dateFormatter = dateFormatter;
        this.clock = clock;
    }
    @SuppressWarnings("ResourceType")
    @Override public void initialize(AddBirthdayContract.FragmentView fragmentView, Bundle args) {
        Timber.d("initialize");
        this.fragmentView = fragmentView;
        LocalDate now = clock.now();
        birthdayBuilder.set(args);
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
        File picture = null;
        if (birthdayBuilder.canBuild()) {
            picture = pictureRepo.getPicture(birthdayBuilder.build());
        }
        fragmentView.initialize(firstName, lastName, formattedDate, selectedYear, selectedMonth, selectedDay, picture);
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
        Timber.d("setName %s", name);
        birthdayBuilder.setName(name);
    }

    private void setLastName(String name) {
        birthdayBuilder.setLastName(name);
    }

    @Override
    public void setInputObservables(Observable<CharSequence> nameChangeEvents,
                                    Observable<CharSequence> lastNameChangeEvents,
                                    Observable<CharSequence> dateChangeEvents) {
        Timber.d("setInputObservables");
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
        Timber.d("saveBirthday");
        //save to repo
        Birthday birthday = birthdayBuilder.build();
        if (birthday != null) {
            transaction.add(birthday).commit();
            if (picture !=null) {
                picture = pictureRepo.storePicture(birthday, picture);
            }
            fragmentView.showSavedBirthday(birthday);
        }
        else {
            fragmentView.showNothingSaved();
        }
        subcription.unsubscribe();
    }

    @Override public void setPicture() {
        Timber.d("setPicture");
        fragmentView.showPicture(picture);
    }
    @Override public void clearPicture() {
        Timber.d("clearPicture");
        picture = null;
    }

    @Override public void takePicture() {
        Timber.d("takePicture");
        picture = pictureRepo.getPicture();
        fragmentView.takePicture(picture);
    }


    @Override public void saveInstanceState(Bundle outState) {
        Timber.d("saveInstanceState");
        birthdayBuilder.archiveTo(outState);

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
