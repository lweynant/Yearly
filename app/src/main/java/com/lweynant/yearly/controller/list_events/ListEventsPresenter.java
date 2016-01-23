package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.controller.EventsAdapter;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoListener;
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


    private ListEventsContract.View view;
    private EventRepoTransaction transaction;
    private IEventNotification eventNotification;
    private Subscription subscription;
    private String currentlyUpdatingRepoModifId;
    private final List<IEvent> empyList = new ArrayList<>();
    private LocalDate sortedFrom = new LocalDate(1900, 1, 1);
    private String repoId;

    public ListEventsPresenter(EventRepoTransaction transaction,
                               IEventNotification eventNotification) {
        this.transaction = transaction;
        this.eventNotification = eventNotification;
    }

    @Override public void setView(ListEventsContract.View view) {
        this.view = view;
    }

    @Override public void removeEvent(IEvent event) {
        transaction.remove(event).commit();
        eventNotification.cancel(event.getID());
    }

    @Override public void openEventDetails(IEvent event) {
        view.showEventDetailsUI(event);
    }

    @Override public void loadEvents() {
        view.setProgressIndicator(true);


    }
    private void checkWhetherDataNeedsToBeResorted(LocalDate now, EventRepo repo) {
        Timber.d("checkWhetherDataNeedsToBeResorted");
        if (sortedFrom.isEqual(now) && repo.getModificationId().equals(repoId)) {
            Timber.d("we sorted repo on same day, so nothing to do");
            return;
        } else {
            Timber.d("sort on new date %s and/or id %s", now.toString(), repo.getModificationId());
            onDataSetChanged(repo);
            sortedFrom = now;
            repoId = repo.getModificationId();
        }
    }

    public void onDataSetChanged(EventRepo repo) {
        synchronized (this) {
            Timber.d("onDataSetChanged - getEvents from repo with modif id: %s", repo.getModificationId());
            if (subscription != null && !subscription.isUnsubscribed()) {
                Timber.d("we allready have a subscription - unsubscribe first.. %s", currentlyUpdatingRepoModifId);
                subscription.unsubscribe();
                dataSetUpdateCancelled(currentlyUpdatingRepoModifId);
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
                            updateDataSet(newEvents, modifId);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Timber.e(e, "onError");
                            dataSetUpdateCancelled(modifId);
                        }

                        @Override
                        public void onNext(List<IEvent> iEvents) {
                            Timber.d("onNext");
                            newEvents = iEvents;
                        }
                    });
        }
        //first = false;
        Timber.d("end of onDataSetChanged");

    }
    protected void updateDataSet(List<IEvent> events, String modifId) {
        synchronized (this) {
            Timber.d("updateDataSet %s", modifId);
            if (subscription != null) subscription.unsubscribe();
            view.setProgressIndicator(false);
            view.showEvents(events);
        }
    }

    protected void dataSetUpdateCancelled(String currentlyUpdatingRepoModifId) {
        Timber.d("dataSetUpdateCancelled %s", currentlyUpdatingRepoModifId);
        if (subscription != null) subscription.unsubscribe();
    }

}
