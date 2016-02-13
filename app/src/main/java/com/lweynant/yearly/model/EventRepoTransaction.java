package com.lweynant.yearly.model;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import timber.log.Timber;

public class EventRepoTransaction implements IEventRepoTransaction {
    private final IEventRepoModifier repoModifier;
    private final List<ITransactionItem> committed = new ArrayList<>();


    public EventRepoTransaction(IEventRepoModifier repoModifier){
        Timber.d("create EventRepoTransaction instance");
        this.repoModifier = repoModifier;
    }

    @Override public EventRepoTransaction add(IEvent event){
        Timber.d("added %s", event.toString());
        committed.add(new AddTransactionItem(event));
        return this;
    }

    @Override public EventRepoTransaction remove(IEvent event) {
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
