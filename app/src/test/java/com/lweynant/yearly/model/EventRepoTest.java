package com.lweynant.yearly.model;

import com.google.gson.JsonObject;
import com.lweynant.yearly.IRString;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
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
    @Mock
    IUUID uuid;
    private int nbrOfDaysForNotification;

    @Before
    public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, 1, 23));
        when(clock.timestamp()).thenReturn("timestamp");
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
        IEvent event = new Event(Date.AUGUST, 1, clock, uuid);
        sut.add(event);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toList().toBlocking().single();
        assertThat(list, hasSize(1));
        assertThat(list, containsInAnyOrder(event));
    }

    @Test
    public void getEvents_FromRepoWithNEvent() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.APRIL, 23));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock, uuid);
        IEvent event2 = new Event(Date.AUGUST, 1, clock, uuid);
        IEvent event3 = new Event(Date.NOVEMBER, 8, clock, uuid);
        sut.add(event1).add(event2).add(event3);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toSortedList().toBlocking().single();
        assertThat(list, hasSize(3));
        assertThat(list, containsInAnyOrder(event1, event2, event3));
        assertThat(list, contains(event2, event3, event1));
    }

    @Test
    public void getEvents_RemoveEvent_EmptyList() throws Exception{
        IEvent event = new Event(Date.AUGUST, 4, clock, uuid);
        sut.add(event);
        sut.remove(event);
        List<IEvent> events = sut.getEvents().toList().toBlocking().single();
        assertThat(events, hasSize(0));
    }
    @Test
    public void getEvents_RemoveEvent() throws Exception{
        IEvent event1 = new Event(Date.AUGUST, 4, clock, uuid);
        IEvent event2 = new Event(Date.AUGUST, 4, clock, uuid);
        sut.add(event1).add(event2);
        sut.remove(event1);
        List<IEvent> events = sut.getEvents().toList().toBlocking().single();
        assertThat(events, hasSize(1));
        assertThat(events, contains(event2));
    }


    @Test
    public void getEvents_Upcoming() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock, uuid);
        IEvent event2 = new Event(Date.AUGUST, 1, clock, uuid);
        IEvent event3 = new Event(Date.AUGUST, 2, clock, uuid);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(Date.NOVEMBER, 8, clock, uuid);
        sut.add(event1).add(event2).add(event3).add(event4);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events
                .filter(event -> Event.shouldBeNotified(clock.now(), event))
                .toList().toBlocking().single();
        assertThat(list, hasSize(2));
        assertThat(list, containsInAnyOrder(event2, event3));

    }

    @Test
    public void getEvents_SetAlarmForEventToday() throws Exception{
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 8);
        when(clock.now()).thenReturn(now);
        IEvent event1 = new Event(Date.JANUARY, 5, clock, uuid);
        IEvent event2 = new Event(Date.AUGUST, 8, clock, uuid);
        IEvent event3 = new Event(now.getMonthOfYear(), now.getDayOfMonth(), clock, uuid);
        sut.add(event1).add(event2).add(event3);
        TimeBeforeNotification time = getFirstUpComingEventTimeBeforeNotification(sut.getEvents(), now);
        assertThat(time.getDays(), is(0));
        assertThat(time.getHour(), is(6));
    }
    @Test
   public void getEvents_SetAlarmForEventTomorrow() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock, uuid);
        IEvent event2 = new Event(Date.AUGUST, 1, clock, uuid);
        IEvent event3 = new Event(Date.AUGUST, 2, clock, uuid);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(Date.NOVEMBER, 8, clock, uuid);
        sut.add(event1).add(event2).add(event3).add(event4);
        Observable<IEvent> events = sut.getEvents();
        TimeBeforeNotification timeBeforeNotification = getFirstUpComingEventTimeBeforeNotification(events, clock.now());
        assertThat(timeBeforeNotification.getDays(), is(0));
        assertThat(timeBeforeNotification.getHour(), is(19));

    }

    private TimeBeforeNotification getFirstUpComingEventTimeBeforeNotification(Observable<IEvent> events, final LocalDate from) {
        return events
                .map(event -> Event.timeBeforeNotification(from, event))
                .reduce((currentMin, x) -> TimeBeforeNotification.min(currentMin, x) )
                .toBlocking().single();
    }

    @Test
    public void getEvents_Serialize() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock, uuid);
        IEvent event2 = new Event(Date.AUGUST, 1, clock, uuid);
        IEvent event3 = new Event(Date.AUGUST, 2, clock, uuid);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(Date.NOVEMBER, 8, clock, uuid);
        sut.add(event1).add(event2).add(event3).add(event4);
        Observable<IEvent> events = sut.getEvents();

        JsonObject json = serialize(events);
        assertThatJson(json).node("events").isArray().ofLength(4);

    }

    private JsonObject serialize(Observable<IEvent> events) {
        EventRepoSerializer serializer = new EventRepoSerializer(clock);
        events.subscribe(serializer);
        return serializer.serialized();
    }


}
