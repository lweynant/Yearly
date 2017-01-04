package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;

import rx.Observable;

public class ListItemsObservable implements IListItemsObservable {

    private IGroupEventsStrategy groupEventsStrategy;

    public ListItemsObservable(IGroupEventsStrategy groupEventsStrategy) {
        this.groupEventsStrategy = groupEventsStrategy;
    }

    @Override public Observable<ListEventsContract.ListItem> from(Observable<IEvent> events) {
        Observable<ListEventsContract.ListItem> items =
                events.groupBy(e -> groupEventsStrategy.group(e))
                        .concatMap(grouped -> grouped.map(event -> groupEventsStrategy.createListItem(event))
                                .startWith(groupEventsStrategy.createListItem(grouped.getKey())));
        return items;
    }

}
