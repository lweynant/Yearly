package com.lweynant.yearly.controller.show_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.BirthdayBuilder;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.test_helpers.StubbedBirthdayBuilder;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.lweynant.yearly.test_helpers.StubbedBirthdayBuilder.stubBuilderAndBundleForEvent;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ShowBirthdayPresenterTest {

    private ShowBirthdayPresenter sut;
    @Mock Bundle emptyBundle;
    @Mock Bundle args;
    @Mock ShowBirthdayContract.FragmentView fragmentView;
    @Mock DateFormatter dateFormatter;
    @Mock BirthdayBuilder birthdayBuilder;
    @Mock IClock clock;
    private final LocalDate today = new LocalDate(2016, Date.MARCH, 1);

    @Before public void setUp() {
        sut = new ShowBirthdayPresenter(dateFormatter, birthdayBuilder, clock);
        when(clock.now()).thenReturn(today);
    }

    @Test public void initializeWithEmptyArgs() {
        sut.initialize(fragmentView, emptyBundle);
        verifyZeroInteractions(fragmentView);
    }

    @Test public void initializeWithMinimalInfoArgs() {
        when(dateFormatter.format(Date.MARCH, 21)).thenReturn("21 Maart");
        stubBuilderAndBundleForEvent(birthdayBuilder, args, "Joe", Date.MARCH, 21, clock);
        sut.initialize(fragmentView, args);

        verify(fragmentView).showFirstName("Joe");
        verify(fragmentView).showDate("21 Maart");
        verify(fragmentView).showNameOfDay("Monday");
        verify(fragmentView).showNextEventIn(20);
        verify(fragmentView).showUnknownAge();
        verify(fragmentView, never()).showAge(anyInt());
    }
    @Test public void initializeWithYearOfBirth() {
        when(dateFormatter.format(2000, Date.MARCH, 21)).thenReturn("21 Maart 2000");
        stubBuilderAndBundleForEvent(birthdayBuilder, args, "Joe", 2000, Date.MARCH, 21, clock);
        sut.initialize(fragmentView, args);

        verify(fragmentView).showFirstName("Joe");
        verify(fragmentView).showDate("21 Maart 2000");
        verify(fragmentView).showNameOfDay("Monday");
        verify(fragmentView).showNextEventIn(20);
        verify(fragmentView).showAge(15);
        verify(fragmentView, never()).showUnknownAge();
    }


}
