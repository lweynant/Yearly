package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SortedEventsLoaderTest {

    @Mock IEventRepo repo;
    @Mock IClock clock;
    private SortedEventsLoader sut;
    private LocalDate today;
    @Mock IEventsLoader.Callback callback;

    @Before public void setUp() {
        today = new LocalDate(2015, Date.FEBRUARY, 8);
        when(clock.now()).thenReturn(today);
        Scheduler s = Schedulers.immediate();
        sut = new SortedEventsLoader(repo, s, clock);
    }

    @Test public void loadEmptyRepo() {
        when(repo.getModificationId()).thenReturn("modif id");
        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.empty());
        sut.loadEvents(false, callback);

        List<IEvent> emptyList = new ArrayList<>();
        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(emptyList, "modif id");
    }

    @Test public void loadRepoWithOneEvent() {
        when(repo.getModificationId()).thenReturn("modif id");
        List<IEvent> events = Arrays.asList(mock(IEvent.class));

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
    }
    @Test public void loadRepoWithNEvent() {
        when(repo.getModificationId()).thenReturn("modif id");
        IEvent first = mock(IEvent.class);
        IEvent second = mock(IEvent.class);
        IEvent third = mock(IEvent.class);
        //stub sorting order
        when(first.compareTo(second)).thenReturn(-1);
        when(first.compareTo(third)).thenReturn(-1);
        when(second.compareTo(first)).thenReturn(1);
        when(second.compareTo(third)).thenReturn(-1);
        when(third.compareTo(first)).thenReturn(1);
        when(third.compareTo(second)).thenReturn(1);

        //pass the events not sorted:
        List<IEvent> events = Arrays.asList(second, first, third);

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);

        //verify we received them in order
        List<IEvent> sortedEvents = Arrays.asList(first, second, third);
        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(sortedEvents, "modif id");
    }

    @Test public void loadRepoEventsObservableThrowsError() {
        when(repo.getModificationId()).thenReturn("modif id");

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.error(new Throwable()));
        sut.loadEvents(false, callback);

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingCancelled("modif id");
    }
    @Test public void cancelLoadingEvents() {
        when(repo.getModificationId()).thenReturn("modif id");

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.never());
        sut.loadEvents(false, callback);
        sut.cancelLoadingEvents();

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingCancelled("modif id");
    }

    @Test public void secondLoadRepoIsCachedIfNotModified() {
        when(repo.getModificationId()).thenReturn("modif id");
        List<IEvent> events = Arrays.asList(mock(IEvent.class));

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        reset(callback, repo);

        when(repo.getModificationId()).thenReturn("modif id");
        //second loadEvents should not touch the repo to retrieve events
        sut.loadEvents(false, callback);
        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        verify(repo, never()).getEventsSubscribedOnProperScheduler();
        verify(repo, never()).getEvents();

    }
    @Test public void secondLoadRepoIsNotCachedNotModified() {
        when(repo.getModificationId()).thenReturn("modif id");
        List<IEvent> events = Arrays.asList(mock(IEvent.class));

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        reset(callback, repo);

        when(repo.getModificationId()).thenReturn("other modif id");
        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);
        verify(callback).onEventsLoadingStarted("other modif id");
        verify(callback).onEventsLoadingFinished(events, "other modif id");
        verify(repo).getEventsSubscribedOnProperScheduler();
    }
    @Test public void secondLoadRepoIsNotCachedIfDateChanges() {
        when(repo.getModificationId()).thenReturn("modif id");
        List<IEvent> events = Arrays.asList(mock(IEvent.class));

        when(clock.now()).thenReturn(today);
        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        reset(callback, repo);

        when(clock.now()).thenReturn(today.plusDays(1));
        when(repo.getModificationId()).thenReturn("modif id");
        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);
        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        verify(repo).getEventsSubscribedOnProperScheduler();
    }
    @Test public void secondLoadRepoIsNotCachedForceUpdateIsTrue() {
        when(repo.getModificationId()).thenReturn("modif id");
        List<IEvent> events = Arrays.asList(mock(IEvent.class));

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(false, callback);

        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        reset(callback, repo);

        when(repo.getModificationId()).thenReturn("modif id");
        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(true, callback);
        verify(callback).onEventsLoadingStarted("modif id");
        verify(callback).onEventsLoadingFinished(events, "modif id");
        verify(repo).getEventsSubscribedOnProperScheduler();
    }
    @Test public void ongoingLoadEventsShouldBeCancelledOnNextLoadEvents() {
        when(repo.getModificationId()).thenReturn("first modif id");

        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.never());
        sut.loadEvents(false, callback);
        verify(callback).onEventsLoadingStarted("first modif id");
        verify(callback, never()).onEventsLoadingFinished(anyList(), eq("first modif id"));
        reset(callback, repo);

        //now we do a second loadEvents (the first one should now be cancelled)
        List<IEvent> events = Arrays.asList(mock(IEvent.class));

        when(repo.getModificationId()).thenReturn("second modif id");
        when(repo.getEventsSubscribedOnProperScheduler()).thenReturn(Observable.from(events));
        sut.loadEvents(true, callback);
        verify(callback).onEventsLoadingCancelled("first modif id");
        verify(callback).onEventsLoadingStarted("second modif id");
        verify(callback).onEventsLoadingFinished(events, "second modif id");
        verify(repo).getEventsSubscribedOnProperScheduler();
    }


}
