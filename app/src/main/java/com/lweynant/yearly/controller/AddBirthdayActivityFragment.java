package com.lweynant.yearly.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public class AddBirthdayActivityFragment extends BaseFragment implements DateSelector.OnClickListener {

    public static final String EXTRA_KEY_BIRTHDAY = "birthday";


    @Inject BirthdayBuilder birthdayBuilder;
    @Inject @Named("birthday_builder") Bundle birthdayBundle;
    @Inject @Named("birthday_builder") Intent resultIntent;
    @Inject IClock clock;
    @Inject IUniqueIdGenerator idGenerator;
    @Bind(R.id.edit_text_birthday_date) EditText dateEditText;
    @Bind(R.id.edit_text_name) EditText nameEditText;
    @Bind(R.id.edit_text_lastname) EditText lastNameEditText;
    private View fragmentView;
    private CompositeSubscription subscription;
    @Inject DateSelector dateSelector;

    public AddBirthdayActivityFragment() {
    }

    @Override
    protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        if (savedInstanceState != null) {
            birthdayBuilder.set(savedInstanceState);
        }
        birthdayBuilder.archiveTo(birthdayBundle);
        resultIntent.putExtra(EXTRA_KEY_BIRTHDAY, birthdayBundle);

        fragmentView = inflater.inflate(R.layout.fragment_add_birthday, container, false);
        ButterKnife.bind(this, fragmentView);

        dateSelector.prepare(getContext(), this);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentView.requestFocus();
                dateSelector.show();
            }
        });
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
        subscription = new CompositeSubscription();
        Observable<CharSequence> nameObservable = RxTextView.textChangeEvents(nameEditText).skip(1)
                .map(e -> e.text());
        Observable<CharSequence> lastNameObservable = RxTextView.textChangeEvents(lastNameEditText).skip(1)
                .map(e -> e.text());
        Observable<Boolean> validName = nameObservable
                .doOnNext(t -> Timber.d("name text field %s", t))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> validDate = RxTextView.textChangeEvents(dateEditText).skip(1)
                .map(e -> e.text())
                .doOnNext(t -> Timber.d("date text field %s", t))
                .map(t -> t.length())
                .map(l -> l > 0);


        Observable<Boolean> enableSaveButton = Observable.combineLatest(validName, validDate, (a, b) -> a && b);

        subscription.add(nameObservable
                .subscribe(n -> {
                    birthdayBuilder.setName(n.toString());
                    birthdayBuilder.archiveTo(birthdayBundle);
                }));
        subscription.add(lastNameObservable
                .subscribe(n -> {
                    birthdayBuilder.setLastName(n.toString());
                    birthdayBuilder.archiveTo(birthdayBundle);
                }));

        subscription.add(enableSaveButton.distinctUntilChanged()
                .subscribe(enabled -> enableSaveButton(enabled)));

    }

    private void enableSaveButton(Boolean enabled) {
        Timber.d("enableSaveButton %s", enabled ? "true" : "false");
        //todo add a save button in the toolbar
    }


    @Override
    public void onDestroy() {
        Timber.d("onDestroy");
        super.onDestroy();
        if (subscription != null) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Timber.d("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        birthdayBuilder.archiveTo(outState);
    }


    @Override public void onPositiveClick(int year, @Date.Month int month, int day, String date) {
        birthdayBuilder.setYear(year);
        handleSelection(month, day, date);
    }


    @Override public void onPositiveClick(@Date.Month int month, int day, String date) {
        birthdayBuilder.clearYear();
        handleSelection(month, day, date);
    }

    @Override public void onNegativeClick() {

    }

    private void handleSelection(@Date.Month int month, int day, String date) {
        birthdayBuilder.setMonth(month).setDay(day).archiveTo(birthdayBundle);
        dateEditText.setText(date);
    }

}
