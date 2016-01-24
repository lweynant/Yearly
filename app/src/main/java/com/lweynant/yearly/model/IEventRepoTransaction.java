package com.lweynant.yearly.model;

import rx.Observable;

public interface IEventRepoTransaction {
    IEventRepoTransaction add(IEvent event);

    IEventRepoTransaction remove(IEvent event);

    void commit();

    Observable<IEvent> added();

    Observable<IEvent> removed();
}
