package com.lweynant.yearly.model;

import rx.Observable;

public interface IEventRepoTransaction {
    Observable<IEvent> add();

    Observable<IEvent> remove();
}
