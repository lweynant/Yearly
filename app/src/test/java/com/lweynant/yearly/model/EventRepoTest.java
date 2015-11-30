package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.IsNot.not;
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
    private int nbrOfDaysForNotification;

    @Before
    public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, 1, 23));
        nbrOfDaysForNotification = 1;
        sut = new EventRepo();

    }

    @Test
    public void getEvents_FromEmptyRepo() throws Exception {
        Observable<IEvent> eventObservable = sut.getEvents();
        Iterable<IEvent> it = eventObservable.toBlocking().toIterable();
        assertThat(it, is(emptyIterable()));
    }

    @Test
    public void getEvents_FromRepoWith1Event() throws Exception {
        IEvent event = new Event(Date.AUGUST, 1, clock);
        sut.add(event);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toList().toBlocking().single();
        assertThat(list, hasSize(1));
        assertThat(list, containsInAnyOrder(event));
    }

    @Test
    public void getEvents_FromRepoWithNEvent() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.APRIL, 23));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock);
        IEvent event2 = new Event(Date.AUGUST, 1, clock);
        IEvent event3 = new Event(Date.NOVEMBER, 8, clock);
        sut.add(event1).add(event2).add(event3);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toSortedList().toBlocking().single();
        assertThat(list, hasSize(3));
        assertThat(list, containsInAnyOrder(event1, event2, event3));
        assertThat(list, contains(event2, event3, event1));
    }

    @Test
    public void getEvents_Upcoming() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock);
        IEvent event2 = new Event(Date.AUGUST, 1, clock);
        IEvent event3 = new Event(Date.AUGUST, 2, clock);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(Date.NOVEMBER, 8, clock);
        sut.add(event1).add(event2).add(event3).add(event4);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events
                .filter(new Func1<IEvent, Boolean>() {
                    @Override
                    public Boolean call(IEvent event) {
                        return Event.shouldBeNotified(clock.now(), event);
                    }
                })
                .toList().toBlocking().single();
        assertThat(list, hasSize(2));
        assertThat(list, containsInAnyOrder(event2, event3));

    }

    @Test
    public void getEvents_SetAlarmForEventToday() throws Exception{
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 8);
        when(clock.now()).thenReturn(now);
        IEvent event1 = new Event(Date.JANUARY, 5, clock);
        IEvent event2 = new Event(Date.AUGUST, 8, clock);
        IEvent event3 = new Event(now.getMonthOfYear(), now.getDayOfMonth(), clock);
        sut.add(event1).add(event2).add(event3);
        TimeBeforeNotification time = getFirstUpComingEventTimeBeforeNotification(sut.getEvents(), now);
        assertThat(time.getDays(), is(0));
        assertThat(time.getHour(), is(6));
    }
    @Test
    public void getEvents_SetAlarmForEventTomorrow() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock);
        IEvent event2 = new Event(Date.AUGUST, 1, clock);
        IEvent event3 = new Event(Date.AUGUST, 2, clock);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(Date.NOVEMBER, 8, clock);
        sut.add(event1).add(event2).add(event3).add(event4);
        Observable<IEvent> events = sut.getEvents();
        TimeBeforeNotification timeBeforeNotification = getFirstUpComingEventTimeBeforeNotification(events, clock.now());
        assertThat(timeBeforeNotification.getDays(), is(0));
        assertThat(timeBeforeNotification.getHour(), is(19));

    }

    private TimeBeforeNotification getFirstUpComingEventTimeBeforeNotification(Observable<IEvent> events, final LocalDate from) {
        return events
                .map(new Func1<IEvent, TimeBeforeNotification>() {
                    @Override
                    public TimeBeforeNotification call(IEvent event) {
                        return Event.timeBeforeNotification(from, event);
                    }
                })
                .reduce(new Func2<TimeBeforeNotification, TimeBeforeNotification, TimeBeforeNotification>() {
                    @Override
                    public TimeBeforeNotification call(TimeBeforeNotification currentMin, TimeBeforeNotification number) {
                        return TimeBeforeNotification.min(currentMin, number);
                    }
                })
                .toBlocking().single();
    }
}
