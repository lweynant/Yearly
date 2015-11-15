package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventRepoTest {

    private EventRepo sut;

    @Mock
    IClock clock;
    @Before
    public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, 1, 23));
        sut = new EventRepo(clock);

    }

    @Test
    public void testEmptyRepo() throws Exception {
        List<IEvent> events = sut.getEvents();
        assertTrue(events.isEmpty());
    }

    private IEvent createFakeEvent(@Date.Month int month, int day)
    {
        IEvent event = mock(IEvent.class);
        LocalDate now = clock.now();
        when(event.getDate()).thenReturn(new LocalDate(now.getYear(), month, day));
        return event;
    }

    @Test
    public void testAddEvent() throws Exception {
        IEvent event = createFakeEvent(Date.APRIL, 23);
        sut.add(event);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), event);
    }



    @Test
    public void testAdd2Events() throws Exception {
        IEvent event1 = createFakeEvent(Date.JANUARY, 12);
        IEvent event2 = createFakeEvent(Date.FEBRUARY, 12);
        sut.add(event1);
        sut.add(event2);
        List<IEvent> events = sut.getEvents();
        assertThat(events, hasItem(event1));
        assertThat(events, hasItem(event2));
    }

    @Test
    public void sort2EventsAddedInOrder() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        sut.add(first).add(second);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), first);
        assertSame(events.get(1), second);

    }

    @Test
    public void sort2EventsAddedOutOfOrder() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        sut.add(second).add(first);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), first);
        assertSame(events.get(1), second);
    }

    @Test
    public void sort2EventsSortFromInBetween() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        sut.add(first).add(second);
        sut.sortFrom(Date.FEBRUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), first);
    }

    @Test
    public void sort2EventsInReverseOrderSameMonth() throws Exception {
        IEvent addedFirst = createFakeEvent(Date.JANUARY, 12);
        IEvent addedSecond = createFakeEvent(Date.JANUARY, 11);
        sut.add(addedFirst).add(addedSecond);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), addedSecond);
        assertSame(events.get(1), addedFirst);
    }
    @Test
    public void sort2EventsInOrderSameMonth() throws Exception {
        IEvent addedFirst = createFakeEvent(Date.JANUARY, 12);
        IEvent addedSecond = createFakeEvent(Date.JANUARY, 13);
        sut.add(addedFirst).add(addedSecond);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), addedFirst);
        assertSame(events.get(1), addedSecond);
    }
    @Test
    public void sort3EventsSortFromAfterFirst() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(Date.FEBRUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }

    @Test
    public void sort3EventsSortFromSecond() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDate().getMonthOfYear(), second.getDate().getDayOfMonth());
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }

    @Test
    public void sort3EventsSortFromDayBeforeSecond() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDate().getMonthOfYear(), second.getDate().getDayOfMonth() - 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }

    @Test
    public void sort3EventsSortFromDayAfterSecond() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDate().getMonthOfYear(), second.getDate().getDayOfMonth() + 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), third);
        assertSame(events.get(1), first);
        assertSame(events.get(2), second);
    }

    @Test
    public void getUpcomingEventsOnEmptyRepo() throws Exception {
        List<IEvent> upcoming = sut.getUpcomingEvents();
        assertTrue(upcoming.isEmpty());
    }

    @Test
    public void getUpcomingEvent() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDate().getMonthOfYear(), second.getDate().getDayOfMonth() + 1);
        List<IEvent> events = sut.getUpcomingEvents();
        assertThat(events.size(), is(1));
        assertSame(events.get(0), third);
    }
    @Ignore
    @Test
    public void getUpcomingEventsForNDays() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.MARCH, 7);
        IEvent fourth = createFakeEvent(Date.MARCH, 8);
        sut.add(first).add(second).add(third).add(fourth);
        sut.sortFrom(second.getDate().getMonthOfYear(), second.getDate().getDayOfMonth());
        sut.setNbrOfDaysForUpcomingEvents(2);
        List<IEvent> events = sut.getUpcomingEvents();
        assertThat(events.size(), is(2));
        assertThat(events, hasItem(second));
        assertThat(events, hasItem(third));

    }
    @Test
    public void getUpcomingEvents() throws Exception {
        IEvent first = createFakeEvent(Date.JANUARY, 12);
        IEvent second = createFakeEvent(Date.MARCH, 6);
        IEvent third = createFakeEvent(Date.MARCH, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(first.getDate().getMonthOfYear(), first.getDate().getDayOfMonth() + 1);
        List<IEvent> events = sut.getUpcomingEvents();
        assertThat(events.size(), is(2));
        assertThat(events, hasItem(second));
        assertThat(events, hasItem(third));
    }


}
