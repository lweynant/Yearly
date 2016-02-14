package com.lweynant.yearly.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class Transaction implements ITransaction {
    private final IEventRepoModifier repoModifier;
    private final List<ITransactionItem> committed = new ArrayList<>();
    public abstract class TransactionItem implements ITransaction.ITransactionItem {
        protected IEvent event;

        public TransactionItem(IEvent event) {
            this.event = event;
        }

        @Override public IEvent event() {
            return event;
        }
    }

    class AddTransactionItem extends TransactionItem {

        public AddTransactionItem(IEvent event){
            super(event);

        }
        @Override public Action action() {
            return Action.ADD;
        }

    }
    class RemoveTransactionItem extends TransactionItem {

        public RemoveTransactionItem(IEvent event){
            super(event);
        }

        @Override public Action action() {
            return Action.REMOVE;
        }

    }
    class UpdateTransactionItem extends TransactionItem {

        public UpdateTransactionItem(IEvent event){
            super(event);
        }

        @Override public Action action() {
            return Action.UPDATE;
        }

    }

    public Transaction(IEventRepoModifier repoModifier){
        Timber.d("create Transaction instance");
        this.repoModifier = repoModifier;
    }

    @Override public Transaction add(IEvent event){
        Timber.d("added %s", event.toString());
        committed.add(new AddTransactionItem(event));
        return this;
    }

    @Override public Transaction remove(IEvent event) {
        Timber.d("removed %s", event.toString());
        committed.add(new RemoveTransactionItem(event));
        return this;
    }

    @Override public ITransaction update(IEvent newEvent) {
        Timber.d("update  %s", newEvent);
        committed.add(new UpdateTransactionItem(newEvent));
        return this;
    }


    @Override public void commit(){
        Timber.d("commit");
        if (!committed.isEmpty()) {
            repoModifier.commit(this);
            committed.clear();
        }
    }

    @Override public Observable<ITransactionItem> committed() {
        return Observable.from(committed);
    }

}
