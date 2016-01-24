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

    @Override public EventRepoTransaction add(IEvent event){
        Timber.d("added %s", event.toString());
        addEvent(event);
        return this;
    }

    @Override public EventRepoTransaction remove(IEvent event) {
        Timber.d("removed %s", event.toString());
        removeEvent(event);
        return this;
    }

    @Override public Observable<IEvent> added() {
        return Observable.from(added);
    }

    @Override public Observable<IEvent> removed() {
        return Observable.from(removed);
    }


    private void removeEvent(IEvent event) {
        removed.add(event);
    }

    @Override public void commit(){
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
