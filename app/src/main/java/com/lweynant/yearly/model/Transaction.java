package com.lweynant.yearly.model;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class Transaction implements ITransaction {
    private final IEventRepoModifier repoModifier;
    private final List<ITransactionItem> committed = new ArrayList<>();


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
