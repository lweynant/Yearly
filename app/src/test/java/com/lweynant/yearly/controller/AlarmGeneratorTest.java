package com.lweynant.yearly.controller;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IAlarm;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;

import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlarmGeneratorTest {
    @Mock IAlarm alarm;
    private LocalDate today;
    private LocalDate tomorrow;
    private Collection<IEvent> list;
    private AlarmGenerator sut;

    @Before public void setUp() {
        today = new LocalDate(2015, Date.JANUARY, 10);
        tomorrow = today.plusDays(1);
        list = new ArrayList<>();
        sut = new AlarmGenerator(alarm);
    }

    @Test public void testEmptyList() {
        sut.generate(Observable.from(list), today);
        verify(alarm).clear();
    }

    @Test public void testListWithOneEvent() {
        list.add(createEvent(today));
        sut.generate(Observable.from(list), today);
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
    }

    @Test public void testListWithNEvents() {
        list.add(createEvent(today.plusDays(100)));
        list.add(createEvent(today.plusDays(150)));
        list.add(createEvent(today.plusDays(200)));
        list.add(createEvent(today.plusDays(250)));
        sut.generate(Observable.from(list), today);
        //we schedule an alarm the day before in the evening
        verify(alarm).scheduleAlarm(today.plusDays(99), NotificationTime.EVENING);
        verifyNoMoreInteractions(alarm);
    }
    @Test public void testGenerateFromTomorrow() {
        list.add(createEvent(today));
        list.add(createEvent(tomorrow));
        sut.generate(Observable.from(list), tomorrow);
        verify(alarm).scheduleAlarm(tomorrow, NotificationTime.MORNING);
        verifyNoMoreInteractions(alarm);
    }

    private IEvent createEvent(LocalDate date) {
        IEvent event = mock(IEvent.class);
        when(event.getDate()).thenReturn(date);
        when(event.getNbrOfDaysForNotification()).thenReturn(1);
        return event;
    }
}
