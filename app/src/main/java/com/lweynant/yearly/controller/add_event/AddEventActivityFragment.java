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
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.DateSelector;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;



/**
 * A placeholder fragment containing a simple view.
 */
public class AddEventActivityFragment extends BaseFragment implements DateSelector.OnClickListener, AddEventContract.FragmentView {

    @Bind(R.id.edit_text_event_name) EditText nameEditText;
    @Bind(R.id.edit_text_event_date) EditText dateEditText;
    private View fragmentView;
    @Inject DateSelector dateSelector;
    @Inject AddEventContract.UserActionListener userActionListener;
    private String initialName;
    private String initialFormattedDate;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;


    public static AddEventActivityFragment newInstance(Bundle args) {
        AddEventActivityFragment fragment = new AddEventActivityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public AddEventActivityFragment() {
    }



    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate");
        super.onCreate(savedInstanceState);
        Bundle args = savedInstanceState != null? savedInstanceState: getArguments();
        userActionListener.initialize(this, args);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        fragmentView = inflater.inflate(R.layout.fragment_add_event, container, false);
        ButterKnife.bind(this, fragmentView);
        if (initialFormattedDate != null){
            dateEditText.setText(initialFormattedDate);
            nameEditText.setText(initialName);
        }
        dateSelector.prepare(getContext(), this, selectedYear, selectedMonth, selectedDay);
        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentView.requestFocus();
                dateSelector.show();
            }
        });
        return fragmentView;
    }

    @Override public void onResume() {
        Timber.d("onResume");
        super.onResume();
        userActionListener.setInputObservables(RxTextView.textChangeEvents(nameEditText).skip(1).map(e -> e.text()),
                RxTextView.textChangeEvents(dateEditText).skip(1).map(e -> e.text()));

    }

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        userActionListener.saveInstanceState(outState);
    }

    @Override public void initialize(String name, String formattedDate, int selectedYear, int selectedMonth, int selectedDay) {
        this.initialName = name;
        this.initialFormattedDate = formattedDate;
        this.selectedYear = selectedYear;
        this.selectedMonth = selectedMonth;
        this.selectedDay = selectedDay;
    }

    @Override public void onPositiveClick(int year, @Date.Month int month, int day) {
        userActionListener.setDate(year, month, day);
    }

    @Override public void onPositiveClick(@Date.Month int month, int day) {
        userActionListener.setDate(month, day);
    }

    @Override public void onNegativeClick() {
    }

    @Override public void showDate(String formattedDate) {
        dateEditText.setText(formattedDate);
    }

    @Override public void showSavedEvent(IEvent event) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        event.archiveTo(bundle);
        intent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
        getActivity().setResult(Activity.RESULT_OK, intent);
    }

    @Override public void showNothingSaved() {
        getActivity().setResult(Activity.RESULT_CANCELED);
    }

    @Override public void enableSaveButton(Boolean enabled) {

    }

    @Override public void onBackPressed() {
        userActionListener.saveEvent();
    }

    @Override public void onOptionsItemHomePressed() {
        userActionListener.saveEvent();
    }
}
