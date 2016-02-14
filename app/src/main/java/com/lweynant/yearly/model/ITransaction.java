package com.lweynant.yearly.model;

import rx.Observable;

public interface ITransaction {

    enum Action {
        ADD,
        REMOVE,
        UPDATE,
    }
    interface ITransactionItem {
        ITransaction.Action action();
        IEvent event();
    }


    ITransaction add(IEvent event);

    ITransaction remove(IEvent event);

    ITransaction update(IEvent newEvent);

    void commit();

    Observable<ITransactionItem> committed();

}
