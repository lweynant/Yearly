package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.list_events.ListEventsPresenter;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoTransaction;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SyncWithTestsListEventsPresenter extends ListEventsPresenter {

    private final CountingIdlingResource idlingResource;
    private final List<String> updates = new ArrayList<String>();

    public SyncWithTestsListEventsPresenter(CountingIdlingResource idlingResource, EventRepo eventRepo,  EventRepoTransaction transaction,
                                            IEventNotification eventNotification, IClock clock) {
        super(eventRepo, transaction, eventNotification, clock);
        this.idlingResource = idlingResource;

    }

    @Override public void startLoadingData(EventRepo repo) {
        Timber.d("startLoadingData - %s", repo.getModificationId());
        incrementIdlingResourceCounter(repo.getModificationId());
        super.startLoadingData(repo);
    }


    @Override protected void onDataLoadCancelled(String currentlyUpdatingRepoModifId) {
        super.onDataLoadCancelled(currentlyUpdatingRepoModifId);
        Timber.d("onDataLoadCancelled - %s", currentlyUpdatingRepoModifId);
        decrementIdlingResourceCounter(currentlyUpdatingRepoModifId);
    }

    @Override protected void onDataLoaded(List<IEvent> events, String modifId) {
        super.onDataLoaded(events, modifId);
        Timber.d("onDataLoaded - %s", modifId);
        decrementIdlingResourceCounter(modifId);
    }


    private void incrementIdlingResourceCounter(String modifId) {
        synchronized (this) {
            Timber.d("increment idling counter %s", modifId);
            idlingResource.increment();
            updates.add(modifId);
        }
    }

    private void decrementIdlingResourceCounter(String currentlyUpdatingRepoModifId) {
        synchronized (this) {
            if (updates.contains(currentlyUpdatingRepoModifId)) {
                Timber.d("decrement idling counter %s", currentlyUpdatingRepoModifId);
                idlingResource.decrement();
                updates.remove(currentlyUpdatingRepoModifId);
            }
        }
    }

}
