package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import org.joda.time.LocalDate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;


public class EventRepo {
    public IJsonFileAccessor eventRepoFileAccessor = null;
    private IClock clock = null;
    private final IUniqueIdGenerator uniqueIdGenerator;
    private Set<IEvent> cachedEvents = Collections.synchronizedSet(new HashSet<>());
    private List<IEventRepoListener> listeners = new ArrayList<>();
    private String modificationId;


    public EventRepo(IJsonFileAccessor eventRepoFileAccessor, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        Timber.d("create event repo");
        this.clock = clock;
        this.uniqueIdGenerator = uniqueIdGenerator;
        this.eventRepoFileAccessor = eventRepoFileAccessor;
        this.modificationId = uniqueIdGenerator.getUniqueId();
    }


    public void addListener(IEventRepoListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(){
        modificationId = uniqueIdGenerator.getUniqueId();
        Timber.d("notifyListeners that data set changed %s", modificationId);
        // since onChanged() is implemented by the listener, it could do anything, including
        // removing itself from {@link mObservers} - and that could cause problems if
        // an iterator is used on the ArrayList {@link listeners}.
        // to avoid such problems, just march thru the list in the reverse order.
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onDataSetChanged(this);
        }

    }

    public EventRepo add(IEvent event) {
        Timber.d("add event %s", event.toString());
        int before = cachedEvents.size();
        cachedEvents.add(event);
        if (cachedEvents.size() != before) {
            notifyListeners();
        }
        return this;
    }

    public EventRepo remove(IEvent event) {
        Timber.d("remove event %s", event.toString());
        cachedEvents.remove(event);
        notifyListeners();
        return this;
    }


    public Observable<IEvent> getEvents() {
            if (!cachedEvents.isEmpty()) {
                return getEventsFromCache();
            } else {
                return getEventsFromFile();
            }
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
                                    subscriber.onNext(event);
                                } else {
                                    break;
                                }
                            }
                        }
                        subscriber.onCompleted();

                    } catch (Throwable t) {
                        subscriber.onError(t);
                    }
                }
            }
        });
        return observable;
    }

    public Observable<NotificationTime> notificationTimeForFirstUpcomingEvent(final LocalDate from) {
        Observable<NotificationTime> time = getEvents()
                .map(event -> new NotificationTime(from, event))
                .reduce((currentMin, x) -> NotificationTime.min(currentMin, x));
        return time;
    }

    public void removeListener(IEventRepoListener listener) {
        listeners.remove(listener);
    }

    public String getModificationId() {
        return modificationId;
    }
}
