package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

public class SortedEventsLoader implements IEventsLoader {
    private final IEventRepo repo;
    private final List<IEvent> emptyList = new ArrayList<>();
    private final IClock clock;
    private final Scheduler mainThread;
    private LocalDate sortedFrom;
    private Subscription subscription;
    private Callback callback;
    private String currentlyUpdatingRepoModifId;
    private List<IEvent> cachedEvents;
    private String repoIdFromCachedEvents;

    public SortedEventsLoader(IEventRepo repo, Scheduler mainThread, IClock clock) {
        this.repo = repo;
        this.clock = clock;
        this.mainThread = mainThread;
        this.cachedEvents = emptyList;
    }

    @Override public void cancelLoadingEvents() {
        synchronized (this) {
            cancelOngoingLoadEvents();
        }
    }

    @Override public void loadEvents(boolean forceUpdate, Callback callback) {
        synchronized (this) {
            this.callback = callback;
            cancelOngoingLoadEvents();
            onEventsLoadingStarted();
            if (canUseCachedEvents(forceUpdate)) {
                onEventsLoadingFinished(cachedEvents);
            } else {
                startLoadingData();
            }
        }
    }

    private void cancelOngoingLoadEvents() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            Timber.d("we allready have a subscription - unsubscribe first.. %s", currentlyUpdatingRepoModifId);
            subscription.unsubscribe();
            onEventsLoadingCancelled();
        }
    }


    private boolean canUseCachedEvents(boolean forceUpdate) {
        return !forceUpdate && cachedEvents != emptyList
                && clock.now().isEqual(sortedFrom)
                && repo.getModificationId().equals(repoIdFromCachedEvents);
    }


    private void startLoadingData() {
            Timber.d("startLoadingData - getEvents from repo with modif id: %s", repo.getModificationId());
            Observable<IEvent> eventsObservable = repo.getEventsSubscribedOnProperScheduler();

            subscription = eventsObservable
                    .toSortedList()
                    .first()
                    .observeOn(mainThread)
                    .subscribe(new Subscriber<List<IEvent>>() {
                        public List<IEvent> newEvents = emptyList;

                        @Override
                        public void onCompleted() {
                            onEventsLoadingFinished(newEvents);
                        }

                        @Override
                        public void onError(Throwable e) {
                            onEventsLoadingCancelled();
                        }

                        @Override
                        public void onNext(List<IEvent> iEvents) {
                            newEvents = iEvents;
                        }
                    });
    }

    private void onEventsLoadingStarted() {
        currentlyUpdatingRepoModifId = repo.getModificationId();
        callback.onEventsLoadingStarted(currentlyUpdatingRepoModifId);
    }


    private void onEventsLoadingCancelled() {
        if (subscription != null) subscription.unsubscribe();
        cachedEvents = emptyList;
        repoIdFromCachedEvents = null;
        callback.onEventsLoadingCancelled(currentlyUpdatingRepoModifId);
        currentlyUpdatingRepoModifId = null;
    }

    private void onEventsLoadingFinished(List<IEvent> newEvents) {
        if (subscription != null) subscription.unsubscribe();
        cachedEvents = newEvents;
        repoIdFromCachedEvents = repo.getModificationId();
        currentlyUpdatingRepoModifId = null;
        sortedFrom = clock.now();
        callback.onEventsLoadingFinished(newEvents, repoIdFromCachedEvents);
    }

}
