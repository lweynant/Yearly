package com.lweynant.yearly.model;


import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IJsonFileReader;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.schedulers.Schedulers;
import timber.log.Timber;


public class EventRepo implements IEventRepoModifier, IEventRepo {
    private final IUniqueIdGenerator uniqueIdGenerator;
    public IJsonFileReader eventRepoFileAccessor = null;
    private IClock clock = null;
    private Map<String, IEvent> cachedEvents = Collections.synchronizedMap(new HashMap<String, IEvent>());
    private List<IEventRepoListener> listeners = new ArrayList<>();
    private String modificationId;


    public EventRepo(IJsonFileReader eventRepoFileAccessor, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
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
        switch (transactionItem.action()) {
            case ADD:
                add(transactionItem.event());
                break;
            case REMOVE:
                remove(transactionItem.event());
                break;
            case UPDATE:
                remove(transactionItem.event());
                add(transactionItem.event());
                break;
        }
    }

    @Override public boolean restore(InputStream inputStream)
    {
        synchronized (cachedEvents) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = parser.parse(in).getAsJsonObject();
                cachedEvents = readEventsInMap(getEventsJsonArray(jsonObject));
                in.close();
                notifyListeners();
                return true;
            } catch (Throwable e) {
                Timber.e(e, "could not restore from jsonArray");
                return false;
            }
        }
    }


    @Override public Observable<IEvent> getEvents() {
        Timber.d("getEvents");
        synchronized (cachedEvents) {
            if (cachedEvents.isEmpty()) {
                try {
                    cachedEvents = readEventsInMap(getEventsJsonArray());
                } catch (IOException e) {
                    Timber.e(e, "new events could not be retrieved");
                }
            }
            return getEventsFromCache(cachedEvents);
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
        cachedEvents.put(event.getStringID(), event);
        return this;
    }


    private IEventRepo remove(IEvent event) {
        Timber.d("removed event %s", event.toString());
        cachedEvents.remove(event.getStringID());
        return this;
    }


    private JsonArray getEventsJsonArray() throws IOException {
        JsonObject jsonObject = eventRepoFileAccessor.read();
        if (jsonObject == null) return new JsonArray();
        return getEventsJsonArray(jsonObject);
    }
    private JsonArray getEventsJsonArray(JsonObject jsonObject) {
        JsonArray events = jsonObject.getAsJsonArray("events");
        if (events == null) events = new JsonArray();
        return events;
    }

    @NonNull private Gson getGson() {
        GsonBuilder builder = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Birthday.class, new BirthdayInstanceCreator(clock, uniqueIdGenerator));
        return builder.create();
    }

    private Map<String, IEvent> readEventsInMap(JsonArray jsonArray) throws IOException {

        Gson gson = getGson();
        Map<String, IEvent> cache = new HashMap<String, IEvent>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject eventObj = jsonArray.get(i).getAsJsonObject();
            Event event = gson.fromJson(eventObj, Birthday.class);
            Timber.d("caching event %s", event.toString());
            cache.put(event.getStringID(), event);
        }
        return cache;
    }

    private Observable<IEvent> getEventsFromCache(Map<String, IEvent> cache) {
        Timber.d("getEventsFromCache");
        Observable<IEvent> observable = Observable.create(new Observable.OnSubscribe<IEvent>() {
            @Override
            public void call(Subscriber<? super IEvent> subscriber) {
                    try {
                        if (cache != null) {
                            for (IEvent event : cache.values()) {
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
        });
        return observable;
    }


}
