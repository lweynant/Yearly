package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public class ListItemsFactory implements IListItemsFactory {

    @Override public Observable<ListEventsContract.ListItem> createFrom(Observable<IEvent> events) {
        Observable<ListEventsContract.ListItem> items =
                events.groupBy(e -> e.getDate().getMonthOfYear())
                        .concatMap(grouped -> grouped.map(event -> new ListEventsContract.ListItem(event))
                                .startWith(new ListEventsContract.ListItem(grouped.getKey())));

        return items;
    }
}
