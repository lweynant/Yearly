package com.lweynant.yearly.model;

import java.io.InputStream;

import rx.Observable;

public interface IEventRepo {
    void addListener(IEventRepoListener listener);

    void removeListener(IEventRepoListener listener);

    void restore(InputStream inputStream);

    Observable<IEvent> getEvents();

    Observable<IEvent> getEventsSubscribedOnProperScheduler();

    String getModificationId();
}
