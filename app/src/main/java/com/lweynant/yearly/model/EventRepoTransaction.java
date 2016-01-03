package com.lweynant.yearly.model;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import timber.log.Timber;

public class EventRepoTransaction implements IEventRepoTransaction {
    private final IEventRepoModifier repoModifier;
    private final Set<IEvent> added = new HashSet<>();
    private final Set<IEvent> removed = new HashSet<>();

    public EventRepoTransaction(IEventRepoModifier repoModifier){
        Timber.d("create EventRepoTransaction instance");
        this.repoModifier = repoModifier;
    }

    public EventRepoTransaction add(IEvent event){
        Timber.d("add %s", event.toString());
        addEvent(event);
        return this;
    }

    public EventRepoTransaction remove(IEvent event) {
        Timber.d("remove %s", event.toString());
        removeEvent(event);
        return this;
    }

    @Override public Observable<IEvent> add() {
        return Observable.from(added);
    }

    @Override public Observable<IEvent> remove() {
        return Observable.from(removed);
    }


    private void removeEvent(IEvent event) {
        removed.add(event);
    }

    public void commit(){
        Timber.d("commit");
        if (hasEvents()) {
            repoModifier.commit(this);
            clearEvents();
        }
    }

    private void addEvent(IEvent event) {
        added.add(event);
    }

    private void clearEvents() {
        added.clear();
        removed.clear();
    }

    private boolean hasEvents() {
        return !added.isEmpty() || !removed.isEmpty();
    }

}
