package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.test_helpers.StubbedBundle;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddEventPresenterTest {

    @Mock AddEventContract.FragmentView fragmentView;
    @Mock IDateFormatter dateFormatter;
    @Mock EventBuilder eventBuilder;
    @Mock ITransaction repoTransaction;
    @Mock IClock clock;
    private AddEventPresenter sut;
    @Mock Bundle emptyBundle;
    private LocalDate today;

    @Before public void setUp() {
        //builder and transactions are fluent interfaces we need to mock them
        when(eventBuilder.setName(anyString())).thenReturn(eventBuilder);
        when(eventBuilder.clearYear()).thenReturn(eventBuilder);
        when(eventBuilder.setYear(anyInt())).thenReturn(eventBuilder);
        //noinspection ResourceType
        when(eventBuilder.setMonth(anyInt())).thenReturn(eventBuilder);
        when(eventBuilder.setDay(anyInt())).thenReturn(eventBuilder);
        when(repoTransaction.add(anyObject())).thenReturn(repoTransaction);
        today = new LocalDate(2016, Date.FEBRUARY, 20);
        when(clock.now()).thenReturn(today);
        sut = new AddEventPresenter(eventBuilder, repoTransaction, dateFormatter, clock);
    }

    @Test public void setDate() {
        sut.initialize(fragmentView, emptyBundle);
        when(dateFormatter.format(Date.APRIL, 24)).thenReturn("24 april");
        sut.setDate(Date.APRIL, 24);
        verify(fragmentView).showDate("24 april");
        verify(eventBuilder).setDay(24);
        verify(eventBuilder).setMonth(Date.APRIL);
        verify(eventBuilder).clearYear();
    }

    @Test public void setDateWithYear() {
        sut.initialize(fragmentView, emptyBundle);
        when(dateFormatter.format(2016, Date.JUNE, 30)).thenReturn("30 juni 2016");
        sut.setDate(2016, Date.JUNE, 30);
        verify(fragmentView).showDate("30 juni 2016");
        verify(eventBuilder).setYear(2016);
        verify(eventBuilder).setMonth(Date.JUNE);
        verify(eventBuilder).setDay(30);
    }
    @Test public void setName() {
        sut.setInputObservables(Observable.just("Name of event"), Observable.empty());

        verify(eventBuilder).setName("Name of event");
    }

    @Test public void saveEvent() {
        sut.initialize(fragmentView, emptyBundle);
        Event event = createEvent("An Event");
        when(eventBuilder.build()).thenReturn(event);
        sut.saveEvent();

        verify(fragmentView).showSavedEvent(event);
        verify(repoTransaction).add(event);
        verify(repoTransaction).commit();
    }

    @Test public void saveEvent_NoValidInput() {
        sut.initialize(fragmentView, emptyBundle);
        when(eventBuilder.build()).thenReturn(null);
        sut.saveEvent();

        verifyZeroInteractions(repoTransaction);
        verify(fragmentView).showNothingSaved();
    }
    @Test public void restoreInstanceState_NullBundle() {
        sut.initialize(fragmentView, emptyBundle);

        verify(eventBuilder, never()).set(null);
    }
    @Test public void restoreInstanceState() {
        Bundle bundle = mock(Bundle.class);
        sut.initialize(fragmentView, emptyBundle);

        verify(eventBuilder).set(emptyBundle);
    }


    @Test public void saveInstanceState() {
        Bundle bundle = mock(Bundle.class);
        sut.saveInstanceState(bundle);

        verify(eventBuilder).archiveTo(bundle);
    }

    @Test public void enableSaveButtonWhenAllInputIsValid() {
        sut.initialize(fragmentView, emptyBundle);
        sut.setInputObservables(Observable.just("Event"), Observable.just("valid date"));

        verify(fragmentView).enableSaveButton(true);
    }

    @Test public void initializeWithEmptyArg() {
        when(dateFormatter.format(today.getMonthOfYear(), today.getDayOfMonth())).thenReturn("the date");
        sut.initialize(fragmentView, emptyBundle);

        verify(fragmentView).initialize(null, null, today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());
    }
    @Test public void initializeWithValidEventArg() {
        when(dateFormatter.format(Date.APRIL, 23)).thenReturn("the date");
        Bundle args = createArgsFor("Events name", Date.APRIL, 23);
        sut.initialize(fragmentView, args);

        verify(fragmentView).initialize(("Events name"), ("the date"), today.getYear(), Date.APRIL, 23);
    }
    @Test public void initializeWithValidEventArgWithYear() {
        when(dateFormatter.format(2001, Date.APRIL, 23)).thenReturn("the date");
        Bundle args = createArgsFor("Events name", 2001, Date.APRIL, 23);
        sut.initialize(fragmentView, args);

        verify(fragmentView).initialize(("Events name"), ("the date"), 2001, Date.APRIL, 23);
    }


    private Bundle createArgsFor(String name, int month, int day) {
        return StubbedBundle.createBundleForEvent(name, month, day);
    }
    private Bundle createArgsFor(String name, int year, int month, int day) {
        return StubbedBundle.createBundleForEvent(name, year, month, day);
    }


    private Event createEvent(String name) {
        Event event = mock(Event.class);
        when(event.getName()).thenReturn(name);
        return event;
    }
}
