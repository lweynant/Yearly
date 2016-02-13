package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class EventRepo implements IEventRepoModifier, IEventRepo {
    private final IUniqueIdGenerator uniqueIdGenerator;
    public IJsonFileAccessor eventRepoFileAccessor = null;
    private IClock clock = null;
    private Set<IEvent> cachedEvents = Collections.synchronizedSet(new HashSet<>());
    private List<IEventRepoListener> listeners = new ArrayList<>();
    private String modificationId;


    public EventRepo(IJsonFileAccessor eventRepoFileAccessor, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        Timber.d("create event repo with file accessor %s", eventRepoFileAccessor.toString());
        this.clock = clock;
        this.uniqueIdGenerator = uniqueIdGenerator;
        this.eventRepoFileAccessor = eventRepoFileAccessor;
        this.modificationId = uniqueIdGenerator.getUniqueId();
    }


    @Override public void addListener(IEventRepoListener listener) {
        listeners.add(listener);
    }

    @Override public void removeListener(IEventRepoListener listener) {
        listeners.remove(listener);
    }

    @Override public void commit(ITransaction transaction) {
        Timber.d("commit");
        transaction.committed().subscribe(iTransactionItem -> handle(iTransactionItem));
        notifyListeners();
    }

    private void handle(ITransaction.ITransactionItem transactionItem) {
        switch (transactionItem.action()){
            case ADD:
                add(transactionItem.event());
                break;
            case REMOVE:
                remove(transactionItem.event());
        }
    }

    @Override public Observable<IEvent> getEvents() {
        Timber.d("getEvents");
        if (!cachedEvents.isEmpty()) {
            return getEventsFromCache();
        } else {
            return getEventsFromFile();
        }
    }
    @Override public Observable<IEvent> getEventsSubscribedOnProperScheduler() {
        Timber.d("getEvents on proper scheduler");
        return getEvents().subscribeOn(Schedulers.io());
    }



    @Override public String getModificationId() {
        return modificationId;
    }


    private void notifyListeners() {
        modificationId = uniqueIdGenerator.getUniqueId();
        Timber.d("notifyListeners (%s) that data set changed %s", listeners.size(), modificationId);
        // since onChanged() is implemented by the listener, it could do anything, including
        // removing itself from {@link mObservers} - and that could cause problems if
        // an iterator is used on the ArrayList {@link listeners}.
        // to avoid such problems, just march thru the list in the reverse order.
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onDataSetChanged(this);
        }

    }

    private IEventRepo add(IEvent event) {
        Timber.d("added event %s", event.toString());
        cachedEvents.add(event);
        return this;
    }


    private IEventRepo remove(IEvent event) {
        Timber.d("removed event %s", event.toString());
        cachedEvents.remove(event);
        return this;
    }


    private Observable<IEvent> getEventsFromFile() {
        Timber.d("getEventsFromFile");
        Observable<IEvent> observable = Observable.create(new Observable.OnSubscribe<IEvent>() {
            @Override
            public void call(Subscriber<? super IEvent> subscriber) {
                cachedEvents.clear();
                Set<IEvent> cache = new HashSet<IEvent>();
                try {
                    try {
                        JsonObject jsonObject = eventRepoFileAccessor.read();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("events");
                        if (jsonArray != null) {
                            GsonBuilder builder = new GsonBuilder()
                                    .excludeFieldsWithoutExposeAnnotation()
                                    .registerTypeAdapter(Birthday.class, new BirthdayInstanceCreator(clock, uniqueIdGenerator));
                            Gson gson = builder.create();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject eventObj = jsonArray.get(i).getAsJsonObject();
                                Event event = gson.fromJson(eventObj, Birthday.class);
                                if (!subscriber.isUnsubscribed()) {
                                    Timber.d("calling onNext for %s", event.toString());
                                    cache.add(event);
                                    subscriber.onNext(event);
                                } else {
                                    break;
                                }
                            }
                        }
                        Timber.d("calling onCompleted");
                        subscriber.onCompleted();
                        cachedEvents = Collections.synchronizedSet(cache);
                    } catch (FileNotFoundException e) {
                        Timber.d("file not found, so we assume we have empty list");
                        subscriber.onCompleted();
                    }

                } catch (Throwable t) {
                    subscriber.onError(t);
                }
            }
        });
        return observable;
    }

    private Observable<IEvent> getEventsFromCache() {
        Timber.d("getEventsFromCache");
        Observable<IEvent> observable = Observable.create(new Observable.OnSubscribe<IEvent>() {
            @Override
            public void call(Subscriber<? super IEvent> subscriber) {
                synchronized (cachedEvents) {
                    try {
                        if (cachedEvents != null) {
                            for (IEvent event : cachedEvents) {
                                if (!subscriber.isUnsubscribed()) {
                                    Timber.d("call onNext for %s", event.toString());
                                    subscriber.onNext(event);
                                } else {
                                    break;
                                }
                            }
                        }
                        Timber.d("call onCompleted");
                        subscriber.onCompleted();

                    } catch (Throwable t) {
                        Timber.d("call onError %s", t.toString());
                        subscriber.onError(t);
                    }
                }
            }
        });
        return observable;
    }


}
