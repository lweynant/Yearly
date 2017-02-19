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
    private Bundle originalArgs;
    private boolean modified;


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
        modified = false;
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
    public void setInputObservables(Observable<CharSequence> firstNameChangeEvents,
                                    Observable<CharSequence> lastNameChangeEvents,
                                    Observable<CharSequence> dateChangeEvents) {
        Timber.d("setInputObservables");
        Observable<Boolean> validFirstName = firstNameChangeEvents
                //.doOnNext(t -> System.out.print(String.format("-'%s'-", t.toString())))
                .map(t -> t.length())
                .map(l -> l > 0);
        Observable<Boolean> validLastName = lastNameChangeEvents.map(t -> true);

        subcription.add(Observable.merge(firstNameChangeEvents.skip(1), lastNameChangeEvents.skip(1), dateChangeEvents.skip(1))
                .subscribe(m -> modified(m)));


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

    private void modified(CharSequence m) {
        Timber.d("modified %s", m);
        modified = true;
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
        }
        showBirthday(birthday);
    }

    @Override public boolean isBirthdayModified() {
        Timber.d(("isBirthdayModified"));
        return modified;
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
