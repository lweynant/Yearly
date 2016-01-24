package com.lweynant.yearly;

import android.support.test.espresso.contrib.CountingIdlingResource;

import com.lweynant.yearly.controller.list_events.IEventsLoader;
import com.lweynant.yearly.model.IEvent;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


public class SyncWithTestsEventsLoader implements IEventsLoader, IEventsLoader.Callback {
    private CountingIdlingResource idlingResource;
    private IEventsLoader loader;
    private Callback callback;
    private final List<String> updates = new ArrayList<String>();

    public SyncWithTestsEventsLoader(CountingIdlingResource idlingResource,
                                     IEventsLoader loader) {
        this.idlingResource = idlingResource;
        this.loader = loader;
    }
    @Override public void loadEvents(boolean forceUpdate, Callback callback) {
        this.callback = callback;
        loader.loadEvents(forceUpdate, this);
    }

    @Override public void onEventsLoadingStarted(String modifId) {
        Timber.d("onEventsLoadingStarted - %s", modifId);
        incrementIdlingResourceCounter(modifId);
        callback.onEventsLoadingStarted(modifId);
    }

    @Override public void onEventsLoadingFinished(List<IEvent> events, String modifId) {
        callback.onEventsLoadingFinished(events, modifId);
        Timber.d("onEventsLoadingFinished - %s", modifId);
        decrementIdlingResourceCounter(modifId);

    }

    @Override public void onEventsLoadingCancelled(String currentlyUpdatingRepoModifId) {
        callback.onEventsLoadingCancelled(currentlyUpdatingRepoModifId);
        Timber.d("onEventsLoadingCancelled - %s", currentlyUpdatingRepoModifId);
        decrementIdlingResourceCounter(currentlyUpdatingRepoModifId);
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
