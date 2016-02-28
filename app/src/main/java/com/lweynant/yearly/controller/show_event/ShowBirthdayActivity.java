package com.lweynant.yearly.controller.show_event;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.lweynant.yearly.BaseYearlyAppComponent;
import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.BaseActivity;
import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ShowBirthdayActivity extends BaseActivity {
    private static final int REQUEST_EDIT_EVENT = 1;
    public static final String EXTRA_INITIAL_BIRTHDAY_BUNDLE = "ShowBirthdayActivity.initial.birthday";
    @Inject IEventViewFactory eventViewFactory;
    @Inject BirthdayBuilder birthdayBuilder;
    @Inject DateFormatter dateFormatter;
    @Inject IClock clock;
    @Bind(R.id.text_birthday_date) TextView dateTextView;
    @Bind(R.id.text_birthday_day) TextView dayTextView;
    @Bind(R.id.text_birthday_age) TextView ageTextView;
    @Bind(R.id.text_birthday_in) TextView inTextView;

    @SuppressWarnings("ResourceType") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_birthday);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        Bundle args = getBundle();
        birthdayBuilder.set(args);
        Birthday birthday = birthdayBuilder.build();
        setTitle(birthday.getName());
        LocalDate date = birthday.getDate();
        if (birthday.hasYearOfOrigin()) {
            LocalDate dayOfBirth = new LocalDate(birthday.getYearOfOrigin(), date.getMonthOfYear(), date.getDayOfMonth());
            dateTextView.setText(dateFormatter.format(dayOfBirth.getYear(), dayOfBirth.getMonthOfYear(), dayOfBirth.getDayOfMonth()));
            Years age = Years.yearsBetween(dayOfBirth, clock.now());
            ageTextView.setText(Integer.toString(age.getYears()));
        }
        else {
            dateTextView.setText(dateFormatter.format(date.getMonthOfYear(), date.getDayOfMonth()));
        }
        dayTextView.setText(date.dayOfWeek().getAsText());
        int nbrDays = Days.daysBetween(clock.now(), date).getDays();
        IEventNotificationText notificationText = eventViewFactory.getEventNotificationText(birthday);
        inTextView.setText(notificationText.getHowLongUntilNext());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_edit_birthday);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = getBundle();
                Intent editEventIntent = eventViewFactory.getActivityIntentForEditing(ShowBirthdayActivity.this, args);
                startActivityForResult(editEventIntent, REQUEST_EDIT_EVENT);

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private Bundle getBundle() {
        Intent intent = getIntent();
        Bundle args;
        if(intent.hasExtra(ShowBirthdayActivity.EXTRA_INITIAL_BIRTHDAY_BUNDLE)){
            args = intent.getBundleExtra(ShowBirthdayActivity.EXTRA_INITIAL_BIRTHDAY_BUNDLE);
        }
        else {
            args = new Bundle();
        }
        return args;
    }

    @Override protected void injectDependencies(BaseYearlyAppComponent component) {
        component.inject(this);
    }
}
