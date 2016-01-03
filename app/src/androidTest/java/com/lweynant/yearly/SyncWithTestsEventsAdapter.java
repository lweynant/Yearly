package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.EventsAdapter;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.EventViewFactory;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SyncWithTestsEventsAdapter extends EventsAdapter {
    private final CountingIdlingResource idlingResource;
    private final List<String> updates = new ArrayList<String>();

    public SyncWithTestsEventsAdapter(CountingIdlingResource idlingResource, EventViewFactory viewFactory) {
        super(viewFactory);
        this.idlingResource = idlingResource;

    }

    @Override public void onDataSetChanged(EventRepo repo) {
        Timber.d("onDataSetChanged - %s", repo.getModificationId());
        incrementIdlingResourceCounter(repo.getModificationId());
        super.onDataSetChanged(repo);
    }


    @Override protected void dataSetUpdateCancelled(String currentlyUpdatingRepoModifId) {
        super.dataSetUpdateCancelled(currentlyUpdatingRepoModifId);
        Timber.d("dataSetUpdateCancelled - %s", currentlyUpdatingRepoModifId);
        decrementIdlingResourceCounter(currentlyUpdatingRepoModifId);
    }

    @Override protected void updateDataSet(List<IEvent> events, String modifId) {
        super.updateDataSet(events, modifId);
        Timber.d("updateDataSet - %s", modifId);
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