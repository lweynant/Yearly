package com.lweynant.yearly.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;

import com.lweynant.yearly.R;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DateSelector {

    @Bind(R.id.date_picker) DatePicker datePicker;
    @Bind(R.id.checkbox_add_year) CheckBox yearSelector;
    private final IClock clock;
    private OnClickListener listener;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog datePickerDialog;
    public DateSelector(IClock clock) {
        this.clock = clock;
    }

    public void prepare(Context context, OnClickListener listener, int year, @Date.Month int month, int day) {
        this.listener = listener;
        dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(R.string.select_date);
        View dateSelectionView = LayoutInflater.from(context).inflate(R.layout.date_selection, null);
        ButterKnife.bind(this, dateSelectionView);
        datePicker.init(year, month - 1, day, null);
        hideYear(context, yearSelector.isChecked());
        yearSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideYear(context, yearSelector.isChecked());
            }
        });
        dialogBuilder.setView(dateSelectionView);
        dialogBuilder.setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Timber.d("pressed ok");
                @Date.Month int month = datePicker.getMonth() + 1;
                int day = datePicker.getDayOfMonth();
                String textDate;
                if (yearSelector.isChecked()) {

                    int year = datePicker.getYear();
                    //noinspection ResourceType
                    listener.onPositiveClick(year, month, day);
                } else {
                    //noinspection ResourceType
                    listener.onPositiveClick(month, day);
                }
            }
        });
        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                listener.onNegativeClick();
            }
        });
        datePickerDialog = dialogBuilder.create();

    }

    public void show() {
        datePickerDialog.show();
    }

    private void hideYear(Context context, boolean checked) {
        int year = context.getResources().getIdentifier("android:id/year", null, null);
        if (year != 0) {
            View yearPicker = datePicker.findViewById(year);
            if (yearPicker != null) {
                yearPicker.setVisibility(checked == true ? View.VISIBLE : View.GONE);
            }
        }
    }

    public interface OnClickListener {
        void onPositiveClick(int year, @Date.Month int month, int day);

        void onPositiveClick(@Date.Month int month, int day);

        void onNegativeClick();
    }
}
