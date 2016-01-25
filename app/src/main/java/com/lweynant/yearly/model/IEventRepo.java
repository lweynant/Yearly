package com.lweynant.yearly.model;

import rx.Observable;

public interface IEventRepo {
    void addListener(IEventRepoListener listener);

    void removeListener(IEventRepoListener listener);

    Observable<IEvent> getEvents();

    Observable<IEvent> getEventsSubscribedOnProperScheduler();

    String getModificationId();
}
