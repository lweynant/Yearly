package com.lweynant.yearly.controller;

import android.content.Intent;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.NotificationTime;
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
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventNotifierTest {

    @Mock IEventNotification eventNotification;
    @Mock IEventViewFactory viewFactory;
    @Mock IClock clock;
    @Mock IIntentFactory intentFactory;
    private EventNotifier sut;
    private Collection<IEvent> events;
    private LocalDate today;
    private int nextId;

    @Before public void setUp() {
        events = new ArrayList<>();
        sut = new EventNotifier(eventNotification, intentFactory, viewFactory, clock);
        today = new LocalDate(2015, Date.JANUARY, 10);
        when(clock.now()).thenReturn(today);
        nextId = 1;
    }

    @Test public void testEmptyList() {
        sut.notify(Observable.from(events));
        verifyZeroInteractions(eventNotification);
    }

    @Test public void testListWithOneEventForTodayAtStartOfDay() {
        IEventNotificationText notificationText = createNotificationText("today's event");
        Intent todaysIntent = mock(Intent.class);
        IEvent event = createEvent(today, todaysIntent, notificationText);
        events.add(event);
        when(clock.hour()).thenReturn(NotificationTime.START_OF_DAY);
        sut.notify(Observable.from(events));
        verify(eventNotification).notify(event.getID(), todaysIntent, notificationText);
    }
    @Test public void testListWithOneEventForTodayAtMornings() {
        IEventNotificationText notificationText = createNotificationText("today's event");
        Intent todaysIntent = mock(Intent.class);
        IEvent event = createEvent(today, todaysIntent, notificationText);
        events.add(event);
        when(clock.hour()).thenReturn(NotificationTime.MORNING);
        sut.notify(Observable.from(events));
        verify(eventNotification).notify(event.getID(), todaysIntent, notificationText);
    }
    @Test public void testListWithOneEventForTodayAtEvening() {
        IEventNotificationText notificationText = createNotificationText("today's event");
        Intent todaysIntent = mock(Intent.class);
        IEvent event = createEvent(today, todaysIntent, notificationText);
        events.add(event);
        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        sut.notify(Observable.from(events));
        verify(eventNotification, never()).notify(event.getID(), todaysIntent, notificationText);
    }
    @Test public void testEventForTodayAndTomorrowAtMorning() {
        IEventNotificationText todaysText = createNotificationText("today's event");
        Intent todaysIntent = mock(Intent.class);
        IEvent todaysEvent = createEvent(today, todaysIntent, todaysText);
        events.add(todaysEvent);
        IEventNotificationText tomorrowsText = createNotificationText("tomorrow's event");
        Intent tomorrowsIntent = mock(Intent.class);
        IEvent tomorrowsEvent = createEvent(today.plusDays(1), tomorrowsIntent, tomorrowsText);
        events.add(tomorrowsEvent);
        events.add(createEvent(today.plusDays(30), mock(Intent.class), createNotificationText("someday in future")));

        when(clock.hour()).thenReturn(NotificationTime.MORNING);
        sut.notify(Observable.from(events));
        verify(eventNotification).notify(todaysEvent.getID(), todaysIntent, todaysText);
        verifyNoMoreInteractions(eventNotification);
    }

    @Test public void testEventForTodayAndTomorrowAtEvening() {
        IEventNotificationText todaysText = createNotificationText("today's event");
        Intent todaysIntent = mock(Intent.class);
        IEvent todaysEvent = createEvent(today, todaysIntent, todaysText);
        events.add(todaysEvent);
        IEventNotificationText tomorrowsText = createNotificationText("tomorrow's event");
        Intent tomorrowsIntent = mock(Intent.class);
        IEvent tomorrowsEvent = createEvent(today.plusDays(1), tomorrowsIntent, tomorrowsText);
        events.add(tomorrowsEvent);
        events.add(createEvent(today.plusDays(30), mock(Intent.class), createNotificationText("someday in future")));

        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        sut.notify(Observable.from(events));
        verify(eventNotification).notify(tomorrowsEvent.getID(), tomorrowsIntent, tomorrowsText);
        verifyNoMoreInteractions(eventNotification);
    }
    @Test public void testNonEmptyListWithNEventsOnSameDayForNotification() {
        IEventNotificationText firstEventText = createNotificationText("first event");
        Intent firstIntent = mock(Intent.class);
        IEvent todaysEvent = createEvent(today, firstIntent, firstEventText);
        events.add(todaysEvent);
        IEventNotificationText secondEventText = createNotificationText("second event");
        Intent secondIntent = mock(Intent.class);
        IEvent todaysEvent2 = createEvent(today, secondIntent, secondEventText);
        events.add(todaysEvent2);
        events.add(createEvent(today.plusDays(30), mock(Intent.class), createNotificationText("someday in future")));

        when(clock.hour()).thenReturn(NotificationTime.START_OF_DAY);
        sut.notify(Observable.from(events));
        verify(eventNotification).notify(todaysEvent.getID(), firstIntent, firstEventText);
        verify(eventNotification).notify(todaysEvent2.getID(), secondIntent, secondEventText);
        verifyNoMoreInteractions(eventNotification);
    }

    @Test public void testNonEmptyListWithNoEventsUpForNotification() {
        IEventNotificationText notificationText = createNotificationText("today's event");
        events.add(createEvent(today.plusDays(45), mock(Intent.class), notificationText));
        events.add(createEvent(today.plusDays(145), mock(Intent.class), notificationText));
        events.add(createEvent(today.plusDays(245), mock(Intent.class), notificationText));
        sut.notify(Observable.from(events));
        verifyNoMoreInteractions(eventNotification);
    }

    private IEventNotificationText createNotificationText(String text) {
        IEventNotificationText notificationText = mock(IEventNotificationText.class);
        when(notificationText.getText()).thenReturn(text);
        return notificationText;
    }

    private IEvent createEvent(LocalDate date, Intent intent, IEventNotificationText notifText) {
        IEvent event = mock(IEvent.class);
        when(event.getID()).thenReturn(nextId);
        nextId += 1;
        when(event.getDate()).thenReturn(date);
        when(event.getNbrOfDaysForNotification()).thenReturn(1);
        when(viewFactory.getEventNotificationText(event)).thenReturn(notifText);
        when(intentFactory.createNotificationIntent(event)).thenReturn(intent);
        return event;
    }
}
