package com.lweynant.yearly.controller;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;


import rx.Observable;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventNotifierTest {

    @Mock IEventNotification eventNotification;
    @Mock IEventViewFactory viewFactory;
    @Mock IClock clock;
    private EventNotifier sut;
    private Collection<IEvent> events;
    private LocalDate today;

    @Before public void setUp() {
        events = new ArrayList<>();
        sut = new EventNotifier(eventNotification, viewFactory, clock);
        today = new LocalDate(2015, Date.JANUARY, 10);
        when(clock.now()).thenReturn(today);
    }

    @Test public void testEmptyList() {
        sut.notify(Observable.from(events));
        verifyZeroInteractions(eventNotification);
    }

    @Test public void testListWithOneEventForToday() {
        IEventNotificationText notificationText = createNotificationText("today's event");
        IEvent event = createEvent(1, today, notificationText);
        events.add(event);
        sut.notify(Observable.from(events));
        verify(eventNotification).notify(1, notificationText);
    }
    @Test public void testNonEmptyListWithNEventsForNotification() {
        IEventNotificationText todaysText = createNotificationText("today's event");
        IEvent todaysEvent = createEvent(1, today, todaysText);
        events.add(todaysEvent);
        IEventNotificationText tomorrowsText = createNotificationText("tomorrow's event");
        IEvent tomorrowsEvent = createEvent(2, today.plusDays(1), tomorrowsText);
        events.add(tomorrowsEvent);
        events.add(createEvent(3, today.plusDays(30), createNotificationText("someday in future")));

        sut.notify(Observable.from(events));
        verify(eventNotification).notify(1, todaysText);
        verify(eventNotification).notify(2, tomorrowsText);
        verifyNoMoreInteractions(eventNotification);
    }

    @Test public void testNonEmptyListWithNoEventsUpForNotification() {
        IEventNotificationText notificationText = createNotificationText("today's event");
        events.add(createEvent(1, today.plusDays(45), notificationText));
        events.add(createEvent(2, today.plusDays(145), notificationText));
        events.add(createEvent(3, today.plusDays(245), notificationText));
        sut.notify(Observable.from(events));
        verifyNoMoreInteractions(eventNotification);
    }

    private IEventNotificationText createNotificationText(String text) {
        IEventNotificationText notificationText = mock(IEventNotificationText.class);
        when(notificationText.getText()).thenReturn(text);
        return notificationText;
    }

    private IEvent createEvent(int id, LocalDate date, IEventNotificationText notifText) {
        IEvent event = mock(IEvent.class);
        when(event.getID()).thenReturn(id);
        when(event.getDate()).thenReturn(date);
        when(event.getNbrOfDaysForNotification()).thenReturn(1);
        when(viewFactory.getEventNotificationText(event)).thenReturn(notifText);
        return event;
    }
}
