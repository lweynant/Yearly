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
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class EventRepo {
    private EventRepoFileAccessor eventRepoFileAccessor = null;
    private IClock clock = null;
    private IUniqueIdGenerator uniqueIdGenerator = null;
    private List<IEvent> cachedEvents = Collections.synchronizedList(new ArrayList<>());
    private List<IEventRepoListener> listeners = new ArrayList<>();

    public EventRepo(EventRepoFileAccessor eventRepoFileAccessor, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.clock = clock;
        this.uniqueIdGenerator = uniqueIdGenerator;
        this.eventRepoFileAccessor = eventRepoFileAccessor;
    }

    public EventRepo() {

    }

    public void addListener(IEventRepoListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(){
        // since onChanged() is implemented by the listener, it could do anything, including
        // removing itself from {@link mObservers} - and that could cause problems if
        // an iterator is used on the ArrayList {@link listeners}.
        // to avoid such problems, just march thru the list in the reverse order.
        for (int i = listeners.size() - 1; i >= 0; i--) {
            listeners.get(i).onDataSetChanged(this);
        }

    }

    public EventRepo add(IEvent event) {
        cachedEvents.add(event);
        notifyListeners();
        return this;
    }

    public EventRepo remove(IEvent event) {
        cachedEvents.remove(event);
        notifyListeners();
        return this;
    }


    public Observable<IEvent> getEvents() {
            if (!cachedEvents.isEmpty() || eventRepoFileAccessor == null) {
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
                cachedEvents = null;
                ArrayList<IEvent> cache = new ArrayList<IEvent>();
                try {
                    try {
                        JsonObject jsonObject = eventRepoFileAccessor.read();
                        JsonArray jsonArray = jsonObject.getAsJsonArray("events");
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
                        Timber.d("calling onCompleted");
                        subscriber.onCompleted();
                        cachedEvents = Collections.synchronizedList(cache);
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

    public Observable<TimeBeforeNotification> timeBeforeFirstUpcomingEvent(final LocalDate from) {
        Observable<TimeBeforeNotification> time = getEvents()
                .map(event -> Event.timeBeforeNotification(from, event))
                .reduce((currentMin, x) -> TimeBeforeNotification.min(currentMin, x));
        return time;
    }

    public void removeListener(IEventRepoListener listener) {
        listeners.remove(listener);
    }
}
