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


    @Test
    public void testAddEvent() throws Exception {
        FakeEvent event = new FakeEvent(Date.APRIL, 23);
        sut.add(event);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), event);
    }

    @Test
    public void testAdd2Events() throws Exception {
        FakeEvent event1 = new FakeEvent(Date.JANUARY, 12);
        FakeEvent event2 = new FakeEvent(Date.FEBRUARY, 12);
        sut.add(event1);
        sut.add(event2);
        List<IEvent> events = sut.getEvents();
        assertThat(events, hasItem(event1));
        assertThat(events, hasItem(event2));
    }

    @Test
    public void sort2EventsAddedInOrder() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        sut.add(first).add(second);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), first);
        assertSame(events.get(1), second);

    }

    @Test
    public void sort2EventsAddedOutOfOrder() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        sut.add(second).add(first);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), first);
        assertSame(events.get(1), second);
    }

    @Test
    public void sort2EventsSortFromInBetween() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        sut.add(first).add(second);
        sut.sortFrom(Date.FEBRUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), first);
    }

    @Test
    public void sort2EventsInReverseOrderSameMonth() throws Exception {
        FakeEvent addedFirst = new FakeEvent(Date.JANUARY, 12);
        FakeEvent addedSecond = new FakeEvent(Date.JANUARY, 11);
        sut.add(addedFirst).add(addedSecond);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), addedSecond);
        assertSame(events.get(1), addedFirst);
    }
    @Test
    public void sort2EventsInOrderSameMonth() throws Exception {
        FakeEvent addedFirst = new FakeEvent(Date.JANUARY, 12);
        FakeEvent addedSecond = new FakeEvent(Date.JANUARY, 13);
        sut.add(addedFirst).add(addedSecond);
        sut.sortFrom(Date.JANUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), addedFirst);
        assertSame(events.get(1), addedSecond);
    }
    @Test
    public void sort3EventsSortFromAfterFirst() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(Date.FEBRUARY, 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }

    @Test
    public void sort3EventsSortFromSecond() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getMonth(), second.getDay());
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }

    @Test
    public void sort3EventsSortFromDayBeforeSecond() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getMonth(), second.getDay() - 1);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }

    @Test
    public void sort3EventsSortFromDayAfterSecond() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getMonth(), second.getDay() + 1);
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
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.SEPTEMBER, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getMonth(), second.getDay() + 1);
        List<IEvent> events = sut.getUpcomingEvents();
        assertThat(events.size(), is(1));
        assertSame(events.get(0), third);
    }
    @Ignore
    @Test
    public void getUpcomingEventsForNDays() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.MARCH, 7);
        FakeEvent fourth = new FakeEvent(Date.MARCH, 8);
        sut.add(first).add(second).add(third).add(fourth);
        sut.sortFrom(second.getMonth(), second.getDay());
        sut.setNbrOfDaysForUpcomingEvents(2);
        List<IEvent> events = sut.getUpcomingEvents();
        assertThat(events.size(), is(2));
        assertThat(events, hasItem(second));
        assertThat(events, hasItem(third));

    }
    @Test
    public void getUpcomingEvents() throws Exception {
        FakeEvent first = new FakeEvent(Date.JANUARY, 12);
        FakeEvent second = new FakeEvent(Date.MARCH, 6);
        FakeEvent third = new FakeEvent(Date.MARCH, 6);
        sut.add(first).add(second).add(third);
        sut.sortFrom(first.getMonth(), first.getDay() + 1);
        List<IEvent> events = sut.getUpcomingEvents();
        assertThat(events.size(), is(2));
        assertThat(events, hasItem(second));
        assertThat(events, hasItem(third));
    }


}
