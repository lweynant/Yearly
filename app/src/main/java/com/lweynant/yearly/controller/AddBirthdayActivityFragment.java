package com.lweynant.yearly.controller;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.lweynant.yearly.AlarmGenerator;
import com.lweynant.yearly.R;
import com.lweynant.yearly.YearlyApp;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoSerializer;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.EventRepoSerializerToFileDecorator;
import com.lweynant.yearly.util.UUID;

import org.joda.time.LocalDate;

import java.util.Calendar;

import rx.Observable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddBirthdayActivityFragment extends Fragment {

    private EditText dateView;
    private AlertDialog.Builder builder;
    private DatePicker datePicker;
    private AlertDialog datePickerDialog;
    private CheckBox yearSelector;

    private int year = 0;
    private int month = 0;
    private int day = 0;
    private String name = null;
    private EditText nameEditText;

    public AddBirthdayActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View fragmentView = inflater.inflate(R.layout.fragment_add_birthday, container, false);
        nameEditText = (EditText)fragmentView.findViewById(R.id.edit_text_name);
        builder = new AlertDialog.Builder(getContext());
        builder.setTitle("select date");
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
        builder.setView(dateSelectionView);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("pressed ok");
                if (yearSelector.isChecked()) {
                    year = datePicker.getYear();
                }
                month = datePicker.getMonth() + 1;
                day = datePicker.getDayOfMonth();
                int year = yearSelector.isChecked() ? datePicker.getYear() : 2015;
                LocalDate date = new LocalDate(year, datePicker.getMonth() + 1, datePicker.getDayOfMonth());
                dateView.setText(date.toString());
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("cancel");
            }
        });
        datePickerDialog = builder.create();
        dateView = (EditText) fragmentView.findViewById(R.id.edit_text_birthday_date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                datePickerDialog.show();
            }
        });
        return fragmentView;
    }

    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
        initializeEventInfo();
    }

    private void initializeEventInfo() {
        year = 0;
        month = 0;
        day = 0;
        name = null;
    }

    @Override
    public void onPause() {
        Timber.d("onPause");
        super.onPause();
        name = nameEditText.getText().toString();
        if (name != null && !name.isEmpty() && month != 0 && day != 0) {
            Birthday bd;
            Clock clock = new Clock();
            UUID uniqueIdGenerator = new UUID();
            EventRepo repo = ((YearlyApp) getActivity().getApplication()).getRepo();
            if (year == 0) {
                bd = new Birthday(name, month, day, clock, uniqueIdGenerator);
            } else {
                bd = new Birthday(name, year, month, day, clock, uniqueIdGenerator);
            }
            Timber.i("add birthday %s", bd.toString());
            repo.add(bd);
            Toast.makeText(getContext(), "added birthday for " + name, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "nothing added", Toast.LENGTH_SHORT).show();
        }
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
