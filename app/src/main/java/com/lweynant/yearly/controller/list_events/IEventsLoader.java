package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;

import java.util.List;

public interface IEventsLoader {

    void cancelLoadingEvents();

    public interface Callback {
        public void onEventsLoadingStarted(String modifId);
        public void onEventsLoadingFinished(List<IEvent> events, String modifId);
        public void onEventsLoadingCancelled(String currentlyUpdatingRepoModifId);
    }
    void loadEvents(boolean forceUpdate, Callback callback);
}
