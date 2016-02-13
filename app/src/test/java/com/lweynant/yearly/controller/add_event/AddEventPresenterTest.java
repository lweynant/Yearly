package com.lweynant.yearly.controller.add_event;

import android.os.Bundle;

import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventBuilder;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import rx.Observable;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddEventPresenterTest {

    @Mock AddEventContract.FragmentView fragmentView;
    @Mock DateFormatter dateFormatter;
    @Mock EventBuilder eventBuilder;
    @Mock ITransaction repoTransaction;
    private AddEventPresenter sut;

    @Before public void setUp() {
        //builder and transactions are fluent interfaces we need to mock them
        when(eventBuilder.setName(anyString())).thenReturn(eventBuilder);
        when(eventBuilder.clearYear()).thenReturn(eventBuilder);
        when(eventBuilder.setYear(anyInt())).thenReturn(eventBuilder);
        //noinspection ResourceType
        when(eventBuilder.setMonth(anyInt())).thenReturn(eventBuilder);
        when(eventBuilder.setDay(anyInt())).thenReturn(eventBuilder);
        when(repoTransaction.add(anyObject())).thenReturn(repoTransaction);
        sut = new AddEventPresenter(eventBuilder, repoTransaction, dateFormatter);
        sut.restoreFromInstanceState(fragmentView, null);
    }

    @Test public void setDate() {
        when(dateFormatter.format(Date.APRIL, 24)).thenReturn("24 april");
        sut.setDate(Date.APRIL, 24);
        verify(fragmentView).showDate("24 april");
        verify(eventBuilder).setDay(24);
        verify(eventBuilder).setMonth(Date.APRIL);
        verify(eventBuilder).clearYear();
    }

    @Test public void setDateWithYear() {
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
        IEvent event = createEvent("An Event");
        when(eventBuilder.build()).thenReturn(event);
        sut.saveEvent();

        verify(fragmentView).showSavedEvent("An Event");
        verify(repoTransaction).add(event);
        verify(repoTransaction).commit();
    }

    @Test public void saveEvent_NoValidInput() {
        when(eventBuilder.build()).thenReturn(null);
        sut.saveEvent();

        verifyZeroInteractions(repoTransaction);
        verify(fragmentView).showNothingSaved();
    }
    @Test public void restoreInstanceState_NullBundle() {
        sut.restoreFromInstanceState(fragmentView, null);

        verifyZeroInteractions(eventBuilder);
    }
    @Test public void restoreInstanceState() {
        Bundle bundle = mock(Bundle.class);
        sut.restoreFromInstanceState(fragmentView, bundle);

        verify(eventBuilder).set(bundle);
    }


    @Test public void saveInstanceState() {
        Bundle bundle = mock(Bundle.class);
        sut.saveInstanceState(bundle);

        verify(eventBuilder).archiveTo(bundle);
    }

    @Test public void enableSaveButtonWhenAllInputIsValid() {
        sut.setInputObservables(Observable.just("Event"), Observable.just("valid date"));

        verify(fragmentView).enableSaveButton(true);
    }

    private IEvent createEvent(String name) {
        IEvent event = mock(IEvent.class);
        when(event.getName()).thenReturn(name);
        return event;
    }
}
