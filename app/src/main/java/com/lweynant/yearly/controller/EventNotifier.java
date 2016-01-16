package com.lweynant.yearly.controller;

import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.Days;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

public class EventNotifier {
    private final IEventViewFactory viewFactory;
    private final IEventNotification eventNotification;
    private final IClock clock;

    public EventNotifier(IEventNotification eventNotification, IEventViewFactory viewFactory, IClock clock) {
        Timber.d("create EventNotifier instance");
        this.viewFactory = viewFactory;
        this.eventNotification = eventNotification;
        this.clock = clock;
    }

    public void notify(Observable<IEvent> events ) {
        Timber.d("notify");
        Subscription subscription = events
                .filter(event -> shouldBeNotified(event))
                .subscribe(event -> eventNotification.notify(event.getID(), viewFactory.getEventNotificationText(event)));
        subscription.unsubscribe();
    }

    private boolean shouldBeNotified(IEvent event) {
        int days = Days.daysBetween(clock.now(), event.getDate()).getDays();
        return days >= 0 && days <= event.getNbrOfDaysForNotification();
    }


}
