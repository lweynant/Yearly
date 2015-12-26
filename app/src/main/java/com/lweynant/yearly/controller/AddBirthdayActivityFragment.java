package com.lweynant.yearly.controller;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.lweynant.yearly.R;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.UUID;

import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Date;

import timber.log.Timber;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddBirthdayActivityFragment extends Fragment {

    public static final int RESULT_CODE = 1288;
    public static final String EXTRA_KEY_BIRTHDAY = "birthday";
    private EditText dateView;
    private AlertDialog.Builder dialogBuilder;
    private DatePicker datePicker;
    private AlertDialog datePickerDialog;
    private CheckBox yearSelector;

    private BirthdayBuilder birthdayBuilder;

    private EditText nameEditText;
    private View fragmentView;

    public AddBirthdayActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        birthdayBuilder = new BirthdayBuilder(new Clock(), new UUID());
        if (savedInstanceState != null) {
            birthdayBuilder.set(savedInstanceState);
        }
        fragmentView = inflater.inflate(R.layout.fragment_add_birthday, container, false);
        nameEditText = (EditText) fragmentView.findViewById(R.id.edit_text_name);
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
                }
                else {
                    String[] months = getResources().getStringArray(R.array.months_day);
                    textDate = String.format(months[month], day);
                    birthdayBuilder.clearYear();
                }
                //noinspection ResourceType
                birthdayBuilder.setMonth(month);
                birthdayBuilder.setDay(day);
                dateView.setText(textDate);
                sendResult();

            }
        });
        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("cancel");
            }
        });
        datePickerDialog = dialogBuilder.create();
        dateView = (EditText) fragmentView.findViewById(R.id.edit_text_birthday_date);
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentView.requestFocus();
                datePickerDialog.show();
            }
        });

        return fragmentView;
    }

    @Override
    public void onResume() {
        Timber.d("onResume");
        super.onResume();
        sendResult();
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

    private void sendResult() {
        Timber.d("sendResult");
        Intent result = new Intent();
        birthdayBuilder.setName(nameEditText.getText().toString());

        Bundle bundle = new Bundle();
        birthdayBuilder.archiveTo(bundle);
        result.putExtra(EXTRA_KEY_BIRTHDAY, bundle);

        getActivity().setResult(Activity.RESULT_OK, result);
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
