package com.lweynant.yearly.controller.show_event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseFragment;
import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.ui.IEventViewFactory;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ShowBirthdayFragment extends BaseFragment implements ShowBirthdayContract.FragmentView {



    public interface Callback {
        public void setToolbar(Toolbar toolBar);
    }


    private Callback callback;
    private static final int REQUEST_EDIT_EVENT = 1;
    @Inject IEventViewFactory eventViewFactory;
    @Inject BirthdayBuilder birthdayBuilder;
    @Inject IDateFormatter dateFormatter;
    @Inject IClock clock;
    @Inject ShowBirthdayContract.UserActionsListener userActionsListener;
    @Bind(R.id.text_birthday_date) TextView dateTextView;
    @Bind(R.id.text_birthday_day) TextView dayTextView;
    @Bind(R.id.text_birthday_age) TextView ageTextView;
    @Bind(R.id.text_birthday_in) TextView inTextView;
    @Bind(R.id.countdown_days) TextView countDownMonthsTextView;
    @Bind(R.id.countdown_hours) TextView countDownWeeksTextView;
    @Bind(R.id.countdown_minutes) TextView countDownDaysTextView;
    @Bind(R.id.toolbar_layout) CollapsingToolbarLayout toolbarLayout;
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.fab_edit_birthday) FloatingActionButton fab;

    public static ShowBirthdayFragment newInstance(Bundle args) {
        ShowBirthdayFragment fragment = new ShowBirthdayFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        Timber.d("injectDependencies");
        component.inject(this);
    }

    @Override public void onAttach(Context context) {
        Timber.d("onAttach");
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (Callback) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement ShowBirthdayFragment.Callback");
        }
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d("onCreateView");
        View view = inflater.inflate(R.layout.fragment_show_birthday, container, false);
        ButterKnife.bind(this, view);
        callback.setToolbar(toolbar);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userActionsListener.editBirthday();
            }
        });

        userActionsListener.initialize(this, getArguments());
        return view;
    }

    @Override public void showFirstName(String name) {
        toolbarLayout.setTitle(name);
    }

    @Override public void showDate(String date) {
        dateTextView.setText(date);
    }

    @Override public void showAge(int age) {
        ageTextView.setText(Integer.toString(age));
    }

    @Override public void showUnknownAge() {
        ageTextView.setText(R.string.question_mark);
    }

    @Override public void showNextEventIn(int totaldays, int months, int weeks, int days) {
        inTextView.setText(String.format("%d",totaldays));
        countDownDaysTextView.setText(String.format("%02d", days));
        countDownWeeksTextView.setText(String.format("%02d", weeks));
        countDownMonthsTextView.setText(String.format("%02d", months));
    }

    @Override public void showNameOfDay(String day) {
        dayTextView.setText(day);
    }

    @Override public void showEditUI(IEvent event) {
        Bundle bundle = new Bundle();
        event.archiveTo(bundle);
        Intent intent = eventViewFactory.getActivityIntentForEditing(getContext(), bundle);
        startActivityForResult(intent, REQUEST_EDIT_EVENT);
    }


    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EDIT_EVENT) {
            Timber.d("resultCode is REQUEST_EDIT_EVENT");
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getBundleExtra(IEvent.EXTRA_KEY_EVENT);
                userActionsListener.initialize(this, bundle);
            }
        }

    }
}
