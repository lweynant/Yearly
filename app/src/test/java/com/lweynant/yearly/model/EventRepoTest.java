package com.lweynant.yearly.model;

import com.lweynant.yearly.FilterEventsInRange;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import rx.Observable;

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
    @Before
    public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2015, 1, 23));
        sut = new EventRepo();

    }

    private IEvent createFakeEvent(@Date.Month int month, int day)
    {
        IEvent event = mock(IEvent.class);
        LocalDate now = clock.now();
        LocalDate date = new LocalDate(now.getYear(), month, day);
        when(event.getDate()).thenReturn(date);
        when(event.toString()).thenReturn(date.toString());
        return event;
    }


    @Test
    public void getEvents_FromEmptyRepo() throws Exception{
        Observable<IEvent> eventObservable = sut.getEvents();
        Iterable<IEvent> it = eventObservable.toBlocking().toIterable();
        assertThat(it, is(emptyIterable()));
    }
    @Test
    public void getEvents_FromRepoWith1Event() throws Exception{
        IEvent event = new Event(Date.AUGUST, 1, clock);
        sut.add(event);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events.toList().toBlocking().single();
        assertThat(list, hasSize(1));
        assertThat(list, containsInAnyOrder(event));
    }
    @Test
    public void getEvents_FromRepoWithNEvent() throws Exception{
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
    public void getEvents_Upcoming() throws Exception{
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JULY, 31));

        IEvent event1 = new Event(Date.FEBRUARY, 8, clock);
        IEvent event2 = new Event(Date.AUGUST, 1, clock);
        IEvent event3 = new Event(Date.AUGUST, 2, clock);
        IEvent event4 = new Event(Date.NOVEMBER, 8, clock);
        sut.add(event1).add(event2).add(event3);
        Observable<IEvent> events = sut.getEvents();
        List<IEvent> list = events
                .filter(new FilterEventsInRange(clock.now(), 2))
//                .filter(new Func1<IEvent, Boolean>() {
//                    @Override
//                    public Boolean call(IEvent event) {
//                        int days = Days.daysBetween(clock.now(), event.getDate()).getDays();
//                        return days >= 0 && days <= 2;
//                    }
//                })
                .toList().toBlocking().single();
        assertThat(list, hasSize(2));
        assertThat(list, containsInAnyOrder(event2, event3));

    }
}
