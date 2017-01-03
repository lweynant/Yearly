package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.utils.RemoveAction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListEventsPresenterTest {

    @Mock IEventsLoader eventsLoader;
    @Mock RemoveAction removeAction;
    @Mock ITransaction transaction;
    @Mock IEventNotification eventNotification;
    private ListEventsPresenter sut;
    @Mock ListEventsContract.ActivityView activityView;
    @Mock ListEventsContract.FragmentView fragmentView;

    @Before public void setUp() {
        //transaction has fluent api - so we make sure we return the object
        when(transaction.remove(anyObject())).thenReturn(transaction);
        when(transaction.add(anyObject())).thenReturn(transaction);
        sut = new ListEventsPresenter(eventsLoader, removeAction);
        sut.setActivityView(activityView);
        sut.setFragmentView(fragmentView);
    }

    @Test public void testLoadNotes() {
        boolean forceUpdate = false;
        sut.loadEvents(forceUpdate);

        verify(eventsLoader).loadEvents(forceUpdate, sut);
    }

    @Test public void testCancelLoadingEvents() {
        sut.cancelLoadingEvents();

        verify(eventsLoader).cancelLoadingEvents();
    }

    @Test public void testRemoveEvent() {
        int id = 67;
        IEvent event = createEvent(id);
        sut.removeEvent(event);

        verify(eventsLoader).loadEvents(true, sut);
        verify(removeAction).remove(event);
    }

    @Test public void testOpenEventDetails() {
        IEvent event = createEvent();
        sut.openEventDetails(event);

        verify(activityView).showEventDetailsUI(event);
    }

    @Test public void testAddNewBirthday() {
        sut.addNewBirthday();

        verify(activityView).showAddNewBirthdayUI();
    }

    @Test public void testAddNewEvent() {
        sut.addNewEvent();

        verify(activityView).showAddNewEventUI();
    }

    @Test public void testOnEventLoadingStarted() {
        sut.onEventsLoadingStarted("modif-id");

        verify(fragmentView).setProgressIndicator(true);
    }

    @Test public void testOnEventLoadingFinished() {
        Observable<IEvent> events = createEvents();
        sut.onEventsLoadingFinished(events, "modif id");

        verify(fragmentView).showEvents(anyObject());
        verify(fragmentView).setProgressIndicator(false);
    }

    @Test public void testOnEventLoadingCancelled() {
        sut.onEventsLoadingCancelled("modif id");

        verify(fragmentView).setProgressIndicator(false);
    }

    private Observable<IEvent> createEvents() {
        ArrayList<IEvent> events = new ArrayList<>();
        events.add(mock(IEvent.class));
        return Observable.from(events);
    }

    private IEvent createEvent() {
        return mock(IEvent.class);
    }

    private IEvent createEvent(int id) {
        IEvent event = mock(IEvent.class);
        when(event.getID()).thenReturn(id);
        return event;
    }
}
