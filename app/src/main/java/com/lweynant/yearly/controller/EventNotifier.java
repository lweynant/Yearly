package com.lweynant.yearly.controller;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.Days;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

public class EventNotifier {
    private final IEventViewFactory viewFactory;
    private final IEventNotification eventNotification;
    private final IClock clock;
    private final IIntentFactory intentFactory;

    public EventNotifier(IEventNotification eventNotification, IIntentFactory intentFactory,
                         IEventViewFactory viewFactory, IClock clock) {
        Timber.d("create EventNotifier instance");
        this.viewFactory = viewFactory;
        this.eventNotification = eventNotification;
        this.clock = clock;
        this.intentFactory = intentFactory;
    }

    public void notify(Observable<IEvent> events ) {
        Timber.d("notify");
        Subscription subscription = events
                .filter(event -> shouldBeNotified(event))
                .subscribe(event -> eventNotification.notify(event.getID(), intentFactory.createNotificationIntent(event),
                        viewFactory.getEventNotificationText(event)));
        subscription.unsubscribe();
    }

    private boolean shouldBeNotified(IEvent event) {
        int days = Days.daysBetween(clock.now(), event.getDate()).getDays();
        return days >= 0 && days <= event.getNbrOfDaysForNotification();
    }


}
