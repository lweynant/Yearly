package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public interface IListItemsObservable {
    public Observable<ListEventsContract.ListItem> from(Observable<IEvent> events);
}
