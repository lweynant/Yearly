package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.IKeyValueArchiver;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IClock;

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
    @Mock DateFormatter dateFormatter;
    @Mock EventBuilder eventBuilder;
    @Mock ITransaction repoTransaction;
    @Mock IClock clock;
    private AddEventPresenter sut;
    private Bundle emptyBundle;
    private LocalDate today;

    @Before public void setUp() {
        emptyBundle = new Bundle();
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
        sut.initialize(fragmentView, emptyBundle, null);
        when(dateFormatter.format(Date.APRIL, 24)).thenReturn("24 april");
        sut.setDate(Date.APRIL, 24);
        verify(fragmentView).showDate("24 april");
        verify(eventBuilder).setDay(24);
        verify(eventBuilder).setMonth(Date.APRIL);
        verify(eventBuilder).clearYear();
    }

    @Test public void setDateWithYear() {
        sut.initialize(fragmentView, emptyBundle, null);
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
        sut.initialize(fragmentView, emptyBundle, null);
        Event event = createEvent("An Event");
        when(eventBuilder.build()).thenReturn(event);
        sut.saveEvent();

        verify(fragmentView).showSavedEvent("An Event");
        verify(repoTransaction).add(event);
        verify(repoTransaction).commit();
    }

    @Test public void saveEvent_NoValidInput() {
        sut.initialize(fragmentView, emptyBundle, null);
        when(eventBuilder.build()).thenReturn(null);
        sut.saveEvent();

        verifyZeroInteractions(repoTransaction);
        verify(fragmentView).showNothingSaved();
    }
    @Test public void restoreInstanceState_NullBundle() {
        sut.initialize(fragmentView, emptyBundle, null);

        verify(eventBuilder, never()).set(null);
    }
    @Test public void restoreInstanceState() {
        Bundle bundle = mock(Bundle.class);
        sut.initialize(fragmentView, emptyBundle, bundle);

        verify(eventBuilder).set(bundle);
    }


    @Test public void saveInstanceState() {
        Bundle bundle = mock(Bundle.class);
        sut.saveInstanceState(bundle);

        verify(eventBuilder).archiveTo(bundle);
    }

    @Test public void enableSaveButtonWhenAllInputIsValid() {
        sut.initialize(fragmentView, emptyBundle, null);
        sut.setInputObservables(Observable.just("Event"), Observable.just("valid date"));

        verify(fragmentView).enableSaveButton(true);
    }

    @Test public void initializeWithEmptyArgAndNullSavedInstanceState() {
        when(dateFormatter.format(today.getMonthOfYear(), today.getDayOfMonth())).thenReturn("the date");
        Bundle emptyArgs = mock(Bundle.class);
        sut.initialize(fragmentView, emptyArgs, null);

        verify(fragmentView).initialize(null, null, today.getYear(), today.getMonthOfYear(), today.getDayOfMonth());
    }
    @Test public void initializeWithValidEventArgAndNullSavedInstanceState() {
        when(dateFormatter.format(Date.APRIL, 23)).thenReturn("the date");
        Bundle args = createArgsFor("Events name", Date.APRIL, 23);
        sut.initialize(fragmentView, args, null);

        verify(fragmentView).initialize(("Events name"), ("the date"), today.getYear(), Date.APRIL, 23);
    }
    @Test public void initializeWithValidEventArgAndSomeSavedInstanceState() {
        when(dateFormatter.format(Date.APRIL, 23)).thenReturn("the date");
        Bundle args = createArgsFor("Events name", Date.APRIL, 23);
        Bundle state = createStateFor("New name", Date.AUGUST, 23);
        sut.initialize(fragmentView, args, state);

        verify(fragmentView).initialize(null, null, today.getYear(), Date.AUGUST, 23);
    }

    private Bundle createStateFor(String name, int month, int day) {
        Bundle bundle = mock(Bundle.class);
        when(bundle.containsKey(IKeyValueArchiver.KEY_NAME)).thenReturn(true);
        when(bundle.getString(IKeyValueArchiver.KEY_NAME)).thenReturn(name);
        when(bundle.containsKey(IKeyValueArchiver.KEY_MONTH)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_MONTH)).thenReturn(month);
        when(bundle.containsKey(IKeyValueArchiver.KEY_DAY)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_DAY)).thenReturn(day);
        return bundle;
    }

    private Bundle createArgsFor(String name, int month, int day) {
        Bundle args = mock(Bundle.class);
        when(args.containsKey(IKeyValueArchiver.KEY_NAME)).thenReturn(true);
        when(args.getString(IKeyValueArchiver.KEY_NAME)).thenReturn(name);
        when(args.containsKey(IKeyValueArchiver.KEY_MONTH)).thenReturn(true);
        when(args.getInt(IKeyValueArchiver.KEY_MONTH)).thenReturn(month);
        when(args.containsKey(IKeyValueArchiver.KEY_DAY)).thenReturn(true);
        when(args.getInt(IKeyValueArchiver.KEY_MONTH)).thenReturn(day);
        //prepare the builder to accept these args
        when(eventBuilder.canBuild()).thenReturn(true);
        Event event = createEvent(name, month, day);
        when(eventBuilder.build()).thenReturn(event);


        return args;
    }

    private Event createEvent(String name, int month, int day) {
        Event event = mock(Event.class);
        when(event.getName()).thenReturn(name);
        LocalDate date = new LocalDate(2016, month, day);
        when(event.getDate()).thenReturn(date);
        return event;
    }

    private Event createEvent(String name) {
        Event event = mock(Event.class);
        when(event.getName()).thenReturn(name);
        return event;
    }
}
