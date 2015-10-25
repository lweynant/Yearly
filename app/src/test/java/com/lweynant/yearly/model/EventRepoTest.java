package com.lweynant.yearly.model;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class EventRepoTest {

    private EventRepo sut;

    @Before
    public void setUp() throws Exception{
        EventRepo.deleteInstance();
        sut = EventRepo.getInstance();
    }
    @Test
    public void testEmptyRepo() throws Exception{
        List<IEvent> events = sut.getEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    public void testSingleton() throws Exception{
        assertTrue(sut instanceof EventRepo);
        EventRepo sut2 = EventRepo.getInstance();
        assertThat(sut, is(sut2));
    }

    @Test
    public void testAddEvent() throws Exception{
        FakeEvent event = new FakeEvent(23, Date.APRIL);
        sut.add(event);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), event);
    }

    @Test
    public void testAdd2Events() throws Exception {
        FakeEvent event1 = new FakeEvent(12, Date.JANUARY);
        FakeEvent event2 = new FakeEvent(12, Date.FEBRUARY);
        sut.add(event1);
        sut.add(event2);
        List<IEvent> events = sut.getEvents();
        assertThat(events, hasItem(event1));
        assertThat(events, hasItem(event2));
    }

    @Test
    public void sort2EventsAddedInOrder() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        sut.add(first).add(second);
        sut.sortFrom(1, Date.JANUARY);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), first);
        assertSame(events.get(1), second);

    }
    @Test
    public void sort2EventsAddedOutOfOrder() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        sut.add(second).add(first);
        sut.sortFrom(1, Date.JANUARY);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), first);
        assertSame(events.get(1), second);
    }
    @Test
    public void sort2EventsSortFromInBetween() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        sut.add(first).add(second);
        sut.sortFrom(1, Date.FEBRUARY);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), first);
    }
    @Test
    public void sort3EventsSortFromAfterFirst() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        FakeEvent third = new FakeEvent(6, Date.SEPTEMBER);
        sut.add(first).add(second).add(third);
        sut.sortFrom(1, Date.FEBRUARY);
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }
    @Test
    public void sort3EventsSortFromSecond() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        FakeEvent third = new FakeEvent(6, Date.SEPTEMBER);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDay(), second.getMonth());
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }
    @Test
    public void sort3EventsSortFromDayBeforeSecond() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        FakeEvent third = new FakeEvent(6, Date.SEPTEMBER);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDay() - 1, second.getMonth());
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), second);
        assertSame(events.get(1), third);
        assertSame(events.get(2), first);
    }
    @Test
    public void sort3EventsSortFromDayAfterSecond() throws Exception{
        FakeEvent first = new FakeEvent(12, Date.JANUARY);
        FakeEvent second = new FakeEvent(6, Date.MARCH);
        FakeEvent third = new FakeEvent(6, Date.SEPTEMBER);
        sut.add(first).add(second).add(third);
        sut.sortFrom(second.getDay()+1, second.getMonth());
        List<IEvent> events = sut.getEvents();
        assertSame(events.get(0), third);
        assertSame(events.get(1), first);
        assertSame(events.get(2), second);
    }

    private class FakeEvent implements IEvent{
        private int day;
        private @Date.Month int month;

        public FakeEvent(int day, @Date.Month int month) {
            this.day = day;
            this.month = month;
        }

        @Override
        public String toString() {
            return String.format("%d-%d", day, month);
        }

        @Override
        public String getTitle() {
            return "fake";
        }

        @Override
        public int getDay() {
            return day;
        }

        @Override
        public int getMonth() {
            return month;
        }
    }
}
