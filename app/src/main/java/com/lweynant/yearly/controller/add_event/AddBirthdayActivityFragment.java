package com.lweynant.yearly.controller.add_event;

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
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.ui.DateSelector;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;




public class AddBirthdayActivityFragment extends BaseFragment implements DateSelector.OnClickListener, AddBirthdayContract.FragmentView {

    @Bind(R.id.edit_text_birthday_date) EditText dateEditText;
    @Bind(R.id.edit_text_first_name) EditText nameEditText;
    @Bind(R.id.edit_text_lastname) EditText lastNameEditText;
    private View fragmentView;
    @Inject IClock clock;
    @Inject DateSelector dateSelector;
    @Inject AddBirthdayContract.UserActionsListener userActionsListener;

    public static AddBirthdayActivityFragment newInstance(Bundle args) {
        AddBirthdayActivityFragment fragment = new AddBirthdayActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public AddBirthdayActivityFragment() {
    }

    @Override
    protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        Bundle bundle;
        if (savedInstanceState == null) {
            Timber.d("first time created, use arguments");
            bundle = getArguments();
        }
        else {
            bundle = savedInstanceState;
        }
        userActionsListener.restoreFromSavedInstanceState(this, bundle);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");

        fragmentView = inflater.inflate(R.layout.fragment_add_birthday, container, false);
        ButterKnife.bind(this, fragmentView);

        LocalDate now = clock.now();
        //noinspection ResourceType
        dateSelector.prepare(getContext(), this, now.getYear(), now.getMonthOfYear(), now.getDayOfMonth());
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentView.requestFocus();
                dateSelector.show();
            }
        });
        return fragmentView;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        Timber.d("onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        userActionsListener.setInputObservables(RxTextView.textChangeEvents(nameEditText).skip(1).map(e -> e.text()),
                                                RxTextView.textChangeEvents(lastNameEditText).skip(1).map(e -> e.text()),
                                                RxTextView.textChangeEvents(dateEditText).skip(1).map(e -> e.text()));
    }

    @Override public void enableSaveButton(Boolean enabled) {
        Timber.d("enableSaveButton %s", enabled ? "true" : "false");
        //todo added a save button in the toolbar
    }

    @Override public void showSavedBirthday(String name) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(AddBirthdayContract.EXTRA_KEY_BIRTHDAY, name);
        getActivity().setResult(Activity.RESULT_OK, resultIntent);
    }

    @Override public void showNothingSaved() {
        getActivity().setResult(Activity.RESULT_CANCELED);
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        Timber.d("onSaveInstanceState");
        super.onSaveInstanceState(outState);
        userActionsListener.saveInstanceState(outState);
    }

    @Override public void onPositiveClick(int year, @Date.Month int month, int day) {
        userActionsListener.setDate(year, month, day);
    }

    @Override public void onPositiveClick(@Date.Month int month, int day) {
        userActionsListener.setDate(month, day);
    }

    @Override public void onNegativeClick() {

    }

    @Override public void showDate(String date) {
        dateEditText.setText(date);
    }
}
