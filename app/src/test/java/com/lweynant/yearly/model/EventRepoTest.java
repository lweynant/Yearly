package com.lweynant.yearly.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyIterable.emptyIterable;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventRepoTest {

    @Mock
    IClock clock;
    @Mock
    IUniqueIdGenerator uniqueIdGenerator;
    @Mock
    EventRepoFileAccessor fileAccessor;
    private EventRepo sut;
    private int nbrOfDaysForNotification;
    private String name;
    private EventRepoTransaction transaction;

    @Before
    public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, 1, 23));
        when(clock.timestamp()).thenReturn("timestamp");
        when(uniqueIdGenerator.getUniqueId()).thenReturn("initial id");
        when(fileAccessor.read()).thenReturn(new JsonObject());
        nbrOfDaysForNotification = 1;
        sut = new EventRepo(fileAccessor, clock, uniqueIdGenerator);
        transaction = new EventRepoTransaction(sut);
        name = "event name";

    }

    @Test
    public void getModificationId_FromEmptyRepo() throws Exception {
        String uid = sut.getModificationId();
        assertThat(uid, is("initial id"));
    }

    @Test
    public void getModificationId_AfterAddingEvent() throws Exception {
        IEvent anEvent = createAnEvent();
        when(uniqueIdGenerator.getUniqueId()).thenReturn("id after adding event");
        transaction.add(anEvent).commit();
        assertThat(sut.getModificationId(), is("id after adding event"));
    }

    @Test
    public void getModificationId_AfterRemovingEvent() throws Exception {
        IEvent anEvent = createAnEvent();
        when(uniqueIdGenerator.getUniqueId()).thenReturn("id after removing event");
        transaction.remove(anEvent).commit();
        assertThat(sut.getModificationId(), is("id after removing event"));
    }

    private IEvent createAnEvent() {
        return new Event(name, Date.AUGUST, 2, clock, uniqueIdGenerator);
    }

    @Test
    public void getEvents_FromEmptyRepo() throws Exception {
        Observable<IEvent> eventObservable = sut.getEvents();
        Iterable<IEvent> it = eventObservable.toBlocking().toIterable();
        assertThat(it, is(emptyIterable()));
    }

    @Test
    public void getEvents_FromRepoWith1Event() throws Exception {
        IEvent event = new Event(name, Date.AUGUST, 1, clock, uniqueIdGenerator);
        transaction.add(event).commit();
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toList().toBlocking().single();
        assertThat(list, hasSize(1));
        assertThat(list, containsInAnyOrder(event));
    }

    @Test
    public void getEvents_FromRepoWithNEvent() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.APRIL, 23));

        IEvent event1 = new Event(name, Date.FEBRUARY, 8, clock, uniqueIdGenerator);
        IEvent event2 = new Event(name, Date.AUGUST, 1, clock, uniqueIdGenerator);
        IEvent event3 = new Event(name, Date.NOVEMBER, 8, clock, uniqueIdGenerator);
        transaction.add(event1).add(event2).add(event3).commit();
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toSortedList().toBlocking().single();
        assertThat(list, hasSize(3));
        assertThat(list, containsInAnyOrder(event1, event2, event3));
        assertThat(list, contains(event2, event3, event1));
    }

    @Test
    public void getEvents_RemoveEvent_EmptyList() throws Exception {
        IEvent event = new Event(name, Date.AUGUST, 4, clock, uniqueIdGenerator);
        transaction.add(event).commit();
        transaction.remove(event).commit();
        List<IEvent> events = sut.getEvents().toList().toBlocking().single();
        assertThat(events, hasSize(0));
    }

    @Test
    public void getEvents_RemoveEvent() throws Exception {
        IEvent event1 = new Event(name, Date.AUGUST, 4, clock, uniqueIdGenerator);
        IEvent event2 = new Event(name, Date.AUGUST, 4, clock, uniqueIdGenerator);
        transaction.add(event1).add(event2).commit();
        transaction.remove(event1).commit();
        List<IEvent> events = sut.getEvents().toList().toBlocking().single();
        assertThat(events, hasSize(1));
        assertThat(events, contains(event2));
    }


    @Test
    public void getEvents_Upcoming() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(name, Date.FEBRUARY, 8, clock, uniqueIdGenerator);
        IEvent event2 = new Event(name, Date.AUGUST, 1, clock, uniqueIdGenerator);
        IEvent event3 = new Event(name, Date.AUGUST, 2, clock, uniqueIdGenerator);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(name, Date.NOVEMBER, 8, clock, uniqueIdGenerator);
        transaction.add(event1).add(event2).add(event3).add(event4).commit();
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events
                .filter(event -> Event.shouldBeNotified(clock.now(), event))
                .toList().toBlocking().single();
        assertThat(list, hasSize(2));
        assertThat(list, containsInAnyOrder(event2, event3));

    }

    @Test
    public void getEvents_SetAlarmForEventToday() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 8);
        when(clock.now()).thenReturn(now);
        IEvent event1 = new Event(name, Date.JANUARY, 5, clock, uniqueIdGenerator);
        IEvent event2 = new Event(name, Date.AUGUST, 8, clock, uniqueIdGenerator);
        @SuppressWarnings("ResourceType")
        IEvent event3 = new Event(name, now.getMonthOfYear(), now.getDayOfMonth(), clock, uniqueIdGenerator);
        transaction.add(event1).add(event2).add(event3).commit();
        NotificationTime time = getFirstUpComingEventTimeBeforeNotification(sut.getEvents(), now);
        assertThat(time.getAlarmDate(), is(now));
        assertThat(time.getHour(), is(6));
    }

    @Test
    public void getEvents_SetAlarmForEventTomorrow() throws Exception {
        LocalDate now = new LocalDate(2015, Date.JULY, 31);
        when(clock.now()).thenReturn(now);

        IEvent event1 = new Event(name, Date.FEBRUARY, 8, clock, uniqueIdGenerator);
        IEvent event2 = new Event(name, Date.AUGUST, 1, clock, uniqueIdGenerator);
        IEvent event3 = new Event(name, Date.AUGUST, 2, clock, uniqueIdGenerator);
        event3.setNbrOfDaysForNotification(2);
        IEvent event4 = new Event(name, Date.NOVEMBER, 8, clock, uniqueIdGenerator);
        transaction.add(event1).add(event2).add(event3).add(event4).commit();
        Observable<IEvent> events = sut.getEvents();
        NotificationTime notificationTime = getFirstUpComingEventTimeBeforeNotification(events, now);
        assertThat(notificationTime.getAlarmDate(), is(now));
        assertThat(notificationTime.getHour(), is(19));

    }

    private NotificationTime getFirstUpComingEventTimeBeforeNotification(Observable<IEvent> events, final LocalDate from) {
        return events
                .map(event -> new NotificationTime(from, event))
                .reduce((currentMin, x) -> NotificationTime.min(currentMin, x))
                .toBlocking().single();
    }

    @Test
    public void getEvents_SerializeDeserialize() throws Exception {
        IEvent event1 = createAnEvent("event 1");
        IEvent event2 = createAnEvent("event 2");
        IEvent event3 = createAnEvent("event 3");
        IEvent event4 = createAnEvent("event 4");
        transaction.add(event1).add(event2).add(event3).add(event4).commit();
        Observable<IEvent> events = sut.getEvents();

        JsonObject json = serialize(events);
        assertThatJson(json).node("events").isArray().ofLength(4);
        List<String> names = new ArrayList<>();
        JsonArray array = json.getAsJsonArray("events");
        for (int i = 0; i < 4; i++) {
            names.add(array.get(i).getAsJsonObject().getAsJsonPrimitive(Event.KEY_NAME).getAsString());
        }
        assertThat(names, containsInAnyOrder("event 1", "event 2", "event 3", "event 4"));

        when(fileAccessor.read()).thenReturn(json);
        EventRepo repo = new EventRepo(fileAccessor, clock, uniqueIdGenerator);
        List<String> list = repo
                .getEvents()
                .map(event -> event.getName())
                .toList().toBlocking().single();
        assertThat(list, hasSize(4));
        assertThat(list, containsInAnyOrder("event 1", "event 2", "event 3", "event 4"));
    }

    @Test
    public void addSameEventTwice() throws Exception {
        IEvent event = createAnEvent();
        IEventRepoListener listener = mock(IEventRepoListener.class);
        sut.addListener(listener);
        transaction.add(event).commit();
        transaction.add(event).commit();
        List<IEvent> events = sut.getEvents().toList().toBlocking().single();
        assertThat(events, hasSize(1));
        assertThat(events, contains(event));
        verify(listener, times(2)).onDataSetChanged(sut);
    }

    private IEvent createAnEvent(String name) {
        return new Event(name, Date.DECEMBER, 23, clock, uniqueIdGenerator);
    }

    private JsonObject serialize(Observable<IEvent> events) {
        EventRepoSerializer serializer = new EventRepoSerializer(clock);
        events.subscribe(serializer);
        return serializer.serialized();
    }


}
