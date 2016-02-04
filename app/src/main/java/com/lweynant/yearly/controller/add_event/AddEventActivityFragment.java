package com.lweynant.yearly.controller.add_event;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.model.Date;
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

    public AddEventActivityFragment() {
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        fragmentView = inflater.inflate(R.layout.fragment_add_event, container, false);
        ButterKnife.bind(this, fragmentView);
        userActionListener.restoreFromInstanceState(this, savedInstanceState);
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

    @Override public void onResume() {
        Timber.d("onResume");
        super.onResume();

    }

    @Override public void onPositiveClick(int year, @Date.Month int month, int day) {
        userActionListener.setDate(year, month, day);
    }

    @Override public void onPositiveClick(@Date.Month int month, int day) {
        userActionListener.setDate(month, day);
    }

    @Override public void onNegativeClick() {
    }

    @Override public void showDate(String date) {
        dateEditText.setText(date);
    }
}
