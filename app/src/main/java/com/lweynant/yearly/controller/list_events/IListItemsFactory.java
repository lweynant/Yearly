package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public interface IListItemsFactory {
    public Observable<ListEventsContract.ListItem> createFrom(Observable<IEvent> events);
}
