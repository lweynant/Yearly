package com.lweynant.yearly.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.lweynant.yearly.R;
import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public class AddBirthdayActivityFragment extends BaseFragment {

    public static final String EXTRA_KEY_BIRTHDAY = "birthday";


    @Inject BirthdayBuilder birthdayBuilder;
    @Inject @Named("birthday_builder") Bundle birthdayBundle;
    @Inject @Named("birthday_builder") Intent resultIntent;
    @Inject IClock clock;
    @Inject IUniqueIdGenerator idGenerator;
    private EditText dateEditText;
    private EditText nameEditText;
    private EditText lastNameEditText;
    private AlertDialog.Builder dialogBuilder;
    private DatePicker datePicker;
    private AlertDialog datePickerDialog;
    private CheckBox yearSelector;
    private View fragmentView;
    private CompositeSubscription subscription;

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
        nameEditText = (EditText) fragmentView.findViewById(R.id.edit_text_name);
        lastNameEditText = (EditText) fragmentView.findViewById(R.id.edit_text_lastname);
        dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.select_date);
        View dateSelectionView = inflater.inflate(R.layout.date_selection, null);
        datePicker = (DatePicker) dateSelectionView.findViewById(R.id.date_picker);
        yearSelector = (CheckBox) dateSelectionView.findViewById(R.id.checkbox_add_year);
        hideYear(yearSelector.isChecked());
        yearSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideYear(yearSelector.isChecked());
            }
        });
        dialogBuilder.setView(dateSelectionView);
        dialogBuilder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("pressed ok");
                int month = datePicker.getMonth() + 1;
                int day = datePicker.getDayOfMonth();
                String textDate;
                if (yearSelector.isChecked()) {
                    int year = datePicker.getYear();
                    birthdayBuilder.setYear(year);
                    textDate = getString(R.string.yyy_mm_dd, year, month, day);
                } else {
                    String[] months = getResources().getStringArray(R.array.months_day);
                    textDate = String.format(months[month], day);
                    birthdayBuilder.clearYear();
                }
                //noinspection ResourceType
                birthdayBuilder.setMonth(month);
                birthdayBuilder.setDay(day);
                dateEditText.setText(textDate);
                birthdayBuilder.archiveTo(birthdayBundle);
            }
        });
        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("cancel");
            }
        });
        datePickerDialog = dialogBuilder.create();
        dateEditText = (EditText) fragmentView.findViewById(R.id.edit_text_birthday_date);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentView.requestFocus();
                datePickerDialog.show();
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
    public void onDestroyView() {
        Timber.d("onDestroyView");
        super.onDestroyView();
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


    @Override
    public void onStop() {
        Timber.d("onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        Timber.d("onPause");

        super.onPause();
    }


    private void hideYear(boolean checked) {
        int year = getContext().getResources().getIdentifier("android:id/year", null, null);
        if (year != 0) {
            View yearPicker = datePicker.findViewById(year);
            if (yearPicker != null) {
                yearPicker.setVisibility(checked == true ? View.VISIBLE : View.GONE);
            }
        }
    }


}
