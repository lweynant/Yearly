package com.lweynant.yearly.model;

import rx.Observable;

public interface IEventRepoTransaction {
    enum Action {
        ADD,
        REMOVE
    }
    interface ITransactionItem {
        IEventRepoTransaction.Action action();
        IEvent event();
    }
    class AddTransactionItem implements ITransactionItem {

        private IEvent event;

        public AddTransactionItem(IEvent event){

            this.event = event;
        }
        @Override public Action action() {
            return Action.ADD;
        }

        @Override public IEvent event() {
            return event;
        }
    }
    class RemoveTransactionItem implements ITransactionItem {
        private IEvent event;

        public RemoveTransactionItem(IEvent event){
            this.event = event;
        }

        @Override public Action action() {
            return Action.REMOVE;
        }

        @Override public IEvent event() {
            return event;
        }
    }


    IEventRepoTransaction add(IEvent event);

    IEventRepoTransaction remove(IEvent event);

    void commit();

    Observable<ITransactionItem> committed();

}
