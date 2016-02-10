package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoTransaction;
import com.lweynant.yearly.platform.IEventNotification;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ListEventsPresenterTest {

    @Mock IEventsLoader eventsLoader;
    @Mock IEventRepoTransaction transaction;
    @Mock IEventNotification eventNotification;
    private ListEventsPresenter sut;
    @Mock ListEventsContract.ActivityView activityView;
    @Mock ListEventsContract.FragmentView fragmentView;

    @Before public void setUp() {
        //transaction has fluent api - so we make sure we return the object
        when(transaction.remove(anyObject())).thenReturn(transaction);
        when(transaction.add(anyObject())).thenReturn(transaction);
        sut = new ListEventsPresenter(eventsLoader, transaction, eventNotification);
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
        verify(transaction).remove(event);
        verify(transaction).commit();
        verify(eventNotification).cancel(id);
    }

    @Test public void testOpenEventDetails() {
        IEvent event = createEvent();
        sut.openEventDetails(event);

        verify(fragmentView).showEventDetailsUI(event);
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
        List<IEvent> events = createEvents();
        sut.onEventsLoadingFinished(events, "modif id");

        verify(fragmentView).showEvents(events);
        verify(fragmentView).setProgressIndicator(false);
    }

    @Test public void testOnEventLoadingCancelled() {
        sut.onEventsLoadingCancelled("modif id");

        verify(fragmentView).setProgressIndicator(false);
    }

    private List<IEvent> createEvents() {
        ArrayList<IEvent> events = new ArrayList<>();
        events.add(mock(IEvent.class));
        return events;
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
