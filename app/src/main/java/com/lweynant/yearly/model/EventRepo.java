package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import timber.log.Timber;

public class EventRepo {
    private List<IEvent> events = new ArrayList<>();

    public EventRepo() {
    }

    public EventRepo add(IEvent event) {
        events.add(event);
        return this;
    }

    public Observable<IEvent> getEvents() {
        Observable<IEvent> observable = Observable.create(new Observable.OnSubscribe<IEvent>() {
            @Override
            public void call(Subscriber<? super IEvent> subscriber) {
                try {
                    for (IEvent event : events) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                        else {
                            break;
                        }
                    }
                    subscriber.onCompleted();

                }
                catch (Throwable t){
                    subscriber.onError(t);
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
}
