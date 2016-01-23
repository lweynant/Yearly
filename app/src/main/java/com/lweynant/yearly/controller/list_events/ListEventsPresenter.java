package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;


public class ListEventsPresenter  implements ListEventsContract.UserActionsListener{


    private ListEventsContract.FragmentView fragmentView;
    private final EventRepo eventRepo;
    private EventRepoTransaction transaction;
    private IEventNotification eventNotification;
    private Subscription subscription;
    private String currentlyUpdatingRepoModifId;
    private final List<IEvent> empyList = new ArrayList<>();
    private LocalDate sortedFrom = new LocalDate(1900, 1, 1);
    private String repoId;
    private IClock clock;
    private ListEventsContract.ActivityView activityView;

    public ListEventsPresenter(EventRepo eventRepo, EventRepoTransaction transaction,
                               IEventNotification eventNotification,
                               IClock clock) {
        this.eventRepo = eventRepo;
        this.transaction = transaction;
        this.eventNotification = eventNotification;
        this.clock = clock;
    }

    @Override public void setFragmentView(ListEventsContract.FragmentView fragmentView) {
        this.fragmentView = fragmentView;
    }

    @Override public void setActivityView(ListEventsContract.ActivityView activityView) {
        this.activityView = activityView;
    }

    @Override public void removeEvent(IEvent event) {
        transaction.remove(event).commit();
        eventNotification.cancel(event.getID());
        updateData(true, eventRepo);
    }

    @Override public void openEventDetails(IEvent requestedEvent) {
        fragmentView.showEventDetailsUI(requestedEvent);
    }

    @Override public void loadEvents(boolean forceUpdate) {
        fragmentView.setProgressIndicator(true);
        updateData(forceUpdate, eventRepo);
    }

    @Override public void addEvent(IEvent event) {
        transaction.add(event)
                .commit();
        updateData(true, eventRepo);
        activityView.showEventAdded(event);
    }

    @Override public void addNewBirthday() {
        activityView.showAddNewBirthdayUI();
    }

    @Override public void addNewEvent() {
        activityView.showAddNewEventUI();
    }

    private void updateData(boolean forceUpdate, EventRepo repo) {
        Timber.d("updateData");
        if (!forceUpdate && sortedFrom.isEqual(clock.now()) && repo.getModificationId().equals(repoId)) {
            Timber.d("we sorted repo on same day, so nothing to do");
            return;
        } else {
            Timber.d("sort on new date %s and/or id %s", clock.now().toString(), repo.getModificationId());
            startLoadingData(repo);
            sortedFrom = clock.now();
            repoId = repo.getModificationId();

        }
    }


    protected void startLoadingData(EventRepo repo) {
        synchronized (this) {
            Timber.d("startLoadingData - getEvents from repo with modif id: %s", repo.getModificationId());
            if (subscription != null && !subscription.isUnsubscribed()) {
                Timber.d("we allready have a subscription - unsubscribe first.. %s", currentlyUpdatingRepoModifId);
                subscription.unsubscribe();
                onDataLoadCancelled(currentlyUpdatingRepoModifId);
            }
            currentlyUpdatingRepoModifId = repo.getModificationId();
            Observable<IEvent> eventsObservable = repo.getEventsSubscribedOnProperScheduler();
            subscription = eventsObservable
                    .toSortedList()
                    .first()
                            //.delay(first ? 5000 : 10, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<IEvent>>() {
                        public List<IEvent> newEvents = empyList;
                        private final String modifId = currentlyUpdatingRepoModifId;

                        @Override
                        public void onCompleted() {
                            Timber.d("onCompleted %s", newEvents.toString());
                            onDataLoaded(newEvents, modifId);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "onError");
                            onDataLoadCancelled(modifId);
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
    protected void onDataLoaded(List<IEvent> events, String modifId) {
        synchronized (this) {
            Timber.d("onDataLoaded %s", modifId);
            if (subscription != null) subscription.unsubscribe();
            if (fragmentView != null) {
                fragmentView.setProgressIndicator(false);
                fragmentView.showEvents(events);
            }
        }
    }

    protected void onDataLoadCancelled(String currentlyUpdatingRepoModifId) {
        Timber.d("onDataLoadCancelled %s", currentlyUpdatingRepoModifId);
        if (subscription != null) subscription.unsubscribe();
    }

}
