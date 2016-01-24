package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepoTransaction;
import com.lweynant.yearly.platform.IEventNotification;

import java.util.List;

import timber.log.Timber;


public class ListEventsPresenter implements ListEventsContract.UserActionsListener, IEventsLoader.Callback {


    private ListEventsContract.FragmentView fragmentView;
    private IEventsLoader eventsLoader;
    private IEventRepoTransaction transaction;
    private IEventNotification eventNotification;
    private ListEventsContract.ActivityView activityView;

    public ListEventsPresenter(IEventsLoader eventsLoader, IEventRepoTransaction transaction,
                               IEventNotification eventNotification) {
        this.eventsLoader = eventsLoader;
        this.transaction = transaction;
        this.eventNotification = eventNotification;
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
        eventsLoader.loadEvents(true, this);
    }

    @Override public void openEventDetails(IEvent requestedEvent) {
        fragmentView.showEventDetailsUI(requestedEvent);
    }

    @Override public void loadEvents(boolean forceUpdate) {
        eventsLoader.loadEvents(forceUpdate, this);
    }

    @Override public void addEvent(IEvent event) {
        transaction.add(event)
                .commit();
        eventsLoader.loadEvents(true, this);
        activityView.showEventAdded(event);
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


    @Override public void onEventsLoadingFinished(List<IEvent> events, String modifId) {
        Timber.d("onEventsLoadingFinished %s", modifId);
        fragmentView.setProgressIndicator(false);
        fragmentView.showEvents(events);
    }

    @Override public void onEventsLoadingCancelled(String currentlyUpdatingRepoModifId) {
        Timber.d("onEventsLoadingCancelled %s", currentlyUpdatingRepoModifId);
        fragmentView.setProgressIndicator(false);
    }
}
