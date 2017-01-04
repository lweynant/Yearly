package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.utils.RemoveAction;

import rx.Observable;
import timber.log.Timber;


public class ListEventsPresenter implements ListEventsContract.UserActionsListener, IEventsLoader.Callback {


    private ListEventsContract.FragmentView fragmentView;
    private IEventsLoader eventsLoader;
    private IListItemsObservable listItemsObservable;
    private RemoveAction removeAction;
    private ListEventsContract.ActivityView activityView;

    public ListEventsPresenter(IEventsLoader eventsLoader, IListItemsObservable listItemsObservable, RemoveAction removeAction) {
        this.eventsLoader = eventsLoader;
        this.listItemsObservable = listItemsObservable;
        this.removeAction = removeAction;
    }

    @Override public void setFragmentView(ListEventsContract.FragmentView fragmentView) {
        this.fragmentView = fragmentView;
    }

    @Override public void setActivityView(ListEventsContract.ActivityView activityView) {
        this.activityView = activityView;
    }

    @Override public void removeEvent(IEvent event) {
        removeAction.remove(event);
        eventsLoader.loadEvents(true, this);
    }

    @Override public void openEventDetails(IEvent requestedEvent) {
        activityView.showEventDetailsUI(requestedEvent);
    }

    @Override public void loadEvents(boolean forceUpdate) {
        eventsLoader.loadEvents(forceUpdate, this);
    }

    @Override public void cancelLoadingEvents() {
        eventsLoader.cancelLoadingEvents();
    }


    @Override public void addNewBirthday() {
        activityView.showAddNewBirthdayUI();
    }

    @Override public void addNewEvent() {
        activityView.showAddNewEventUI();
    }

    @Override public void onEventsLoadingStarted(String modifId) {
        fragmentView.setProgressIndicator(true);
    }


    @Override public void onEventsLoadingFinished(Observable<IEvent> events, String modifId) {
        Timber.d("onEventsLoadingFinished %s", modifId);
        fragmentView.setProgressIndicator(false);
        fragmentView.showListItems(listItemsObservable.from(events));
    }

    @Override public void onEventsLoadingCancelled(String currentlyUpdatingRepoModifId) {
        Timber.d("onEventsLoadingCancelled %s", currentlyUpdatingRepoModifId);
        fragmentView.setProgressIndicator(false);
    }
}
