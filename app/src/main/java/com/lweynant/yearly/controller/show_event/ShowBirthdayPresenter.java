package com.lweynant.yearly.controller.show_event;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.ui.IEventViewFactory;
import com.lweynant.yearly.utils.RemoveAction;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Years;

public class ShowBirthdayPresenter implements ShowBirthdayContract.UserActionsListener
{
    private final IEventViewFactory eventViewFactory;
    private IDateFormatter dateFormatter;
    private BirthdayBuilder birthdayBuilder;
    private RemoveAction removeAction;
    private IClock clock;
    private ShowBirthdayContract.FragmentView fragmentView;

    public ShowBirthdayPresenter(IDateFormatter dateFormatter, BirthdayBuilder birthdayBuilder,
                                 RemoveAction removeAction, IEventViewFactory eventViewFactory, IClock clock) {
        this.dateFormatter = dateFormatter;
        this.birthdayBuilder = birthdayBuilder;
        this.removeAction = removeAction;
        this.clock = clock;
        this.eventViewFactory = eventViewFactory;
    }

    @Override public void initialize(ShowBirthdayContract.FragmentView fragmentView, @NonNull Bundle args) {
        this.fragmentView = fragmentView;
        birthdayBuilder.set(args);
        if (birthdayBuilder.canBuild()) {
            Birthday birthday = birthdayBuilder.build();
            fragmentView.showFirstName(birthday.getName());
            fragmentView.showDate(getFormattedDate(birthday));
            showAge(fragmentView, birthday);
            LocalDate date = birthday.getDate();
            showNameOfDay(fragmentView, date);
            showNextEventInNbrDays(fragmentView, date);
        }
    }


    @Override public void editBirthday() {
        IEvent event = birthdayBuilder.build();
        fragmentView.showEditUI(event);
    }

    @Override public void removeBirthday() {
        IEvent event = birthdayBuilder.build();
        removeAction.remove(event);
    }

    @Override public String getTextToShare() {
        Birthday birthday = birthdayBuilder.build();
        IEventNotificationText text = eventViewFactory.getEventNotificationText(birthday);
        return text.getOneLiner();
    }

    public void showNextEventInNbrDays(ShowBirthdayContract.FragmentView fragmentView, LocalDate date) {
        int nbrDays = Days.daysBetween(clock.now(), date).getDays();
        fragmentView.showNextEventIn(nbrDays);
    }

    public void showNameOfDay(ShowBirthdayContract.FragmentView fragmentView, LocalDate date) {
        fragmentView.showNameOfDay(date.dayOfWeek().getAsText());
    }

    private void showAge(ShowBirthdayContract.FragmentView fragmentView, Birthday birthday) {
        if (birthday.hasYearOfOrigin()) {
            LocalDate date = birthday.getDate();
            LocalDate dayOfBirth = new LocalDate(birthday.getYearOfOrigin(), date.getMonthOfYear(), date.getDayOfMonth());

            Years age = Years.yearsBetween(dayOfBirth, clock.now());
            fragmentView.showAge(age.getYears());
        }
        else {
            fragmentView.showUnknownAge();
        }
    }

    @SuppressWarnings("ResourceType") private String getFormattedDate(Birthday birthday) {
        LocalDate date = birthday.getDate();
        if (birthday.hasYearOfOrigin()) {
            return dateFormatter.format(birthday.getYearOfOrigin(), date.getMonthOfYear(), date.getDayOfMonth());
        }
        else {
            return dateFormatter.format(date.getMonthOfYear(), date.getDayOfMonth());
        }
    }
}
