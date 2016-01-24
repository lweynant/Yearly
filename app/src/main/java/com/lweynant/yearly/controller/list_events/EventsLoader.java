package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class EventsLoader implements IEventsLoader {
    private EventRepo repo;
    private final List<IEvent> empyList = new ArrayList<>();
    private LocalDate sortedFrom = new LocalDate(1900, 1, 1);
    private String repoId;
    private IClock clock;
    private Subscription subscription;
    private Callback callback;
    private String currentlyUpdatingRepoModifId;
    private List<IEvent> events;
    //private boolean first = true;

    public EventsLoader(EventRepo repo, IClock clock) {
        this.repo = repo;
        this.clock = clock;
        events = empyList;
    }

    @Override public void cancelLoadingEvents() {
        synchronized (this) {
            if (subscription != null && !subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }
    }

    @Override public void loadEvents(boolean forceUpdate, Callback callback) {
        this.callback = callback;
        updateData(forceUpdate);
    }

    private void updateData(boolean forceUpdate) {
        Timber.d("updateData");
        onEventsLoadingStarted(repo.getModificationId());
        if (!forceUpdate && events != empyList && sortedFrom.isEqual(clock.now()) && repo.getModificationId().equals(repoId)) {
            Timber.d("we sorted repo on same day, so nothing to do");
            onEventsLoadingFinished(events, currentlyUpdatingRepoModifId);
            return;
        } else {
            Timber.d("sort on new date %s and/or id %s", clock.now().toString(), repo.getModificationId());
            startLoadingData(repo.getModificationId());
            sortedFrom = clock.now();
            repoId = repo.getModificationId();

        }
    }


    private void startLoadingData(String modifId) {
        synchronized (this) {

            Timber.d("startLoadingData - getEvents from repo with modif id: %s", modifId);
            if (subscription != null && !subscription.isUnsubscribed()) {
                Timber.d("we allready have a subscription - unsubscribe first.. %s", currentlyUpdatingRepoModifId);
                subscription.unsubscribe();
                onEventsLoadingCancelled(currentlyUpdatingRepoModifId);
            }
            currentlyUpdatingRepoModifId = repo.getModificationId();
            Observable<IEvent> eventsObservable = repo.getEventsSubscribedOnProperScheduler();
            subscription = eventsObservable
                    .toSortedList()
                    .first()
                     //       .delay(first ? 5000 : 10, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<IEvent>>() {
                        public List<IEvent> newEvents = empyList;
                        private final String modifId = currentlyUpdatingRepoModifId;

                        @Override
                        public void onCompleted() {
                            Timber.d("onCompleted %s", newEvents.toString());
                            onEventsLoadingFinished(newEvents, modifId);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "onError");
                            onEventsLoadingCancelled(modifId);
                        }

                        @Override
                        public void onNext(List<IEvent> iEvents) {
                            Timber.d("onNext");
                            newEvents = iEvents;
                        }
                    });
        }
        //first = false;
        Timber.d("end of startLoadingData");

    }

    private void onEventsLoadingStarted(String modificationId) {
        callback.onEventsLoadingStarted(modificationId);
    }


    private void onEventsLoadingCancelled(String modifId) {
        if (subscription != null) subscription.unsubscribe();
        events = empyList;
        callback.onEventsLoadingCancelled(modifId);
    }

    private void onEventsLoadingFinished(List<IEvent> newEvents, String modifId) {
        if (subscription != null) subscription.unsubscribe();
        events = newEvents;
        callback.onEventsLoadingFinished(newEvents, modifId);
    }

}
