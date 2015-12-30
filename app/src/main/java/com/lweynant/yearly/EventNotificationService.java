package com.lweynant.yearly;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class EventNotificationService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NOTIFY = "com.lweynant.yearly.action.ACTION-NOTIFY";

    public EventNotificationService() {
        super("EventNotificationService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startNotification(Context context) {
        Intent intent = new Intent(context, EventNotificationService.class);
        intent.setAction(ACTION_NOTIFY);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("onHandleIntent");
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NOTIFY.equals(action)) {
                handleActionNotification();
            }
        }
    }

    private void handleActionNotification() {
        Timber.d("handleActionNotification");
        YearlyApp app = (YearlyApp) getApplication();

        final IClock clock = app.getComponent().clock();
        EventViewFactory viewFactory = new EventViewFactory(app, clock);
        EventRepo repo = app.getComponent().eventRepo();
        Timber.d("retrieved repo from component %s", repo.toString());

        Observable<IEvent> eventsObservable = repo.getEvents();
        Subscription subscription = eventsObservable
                .filter(event -> Event.shouldBeNotified(clock.now(), event))
                .subscribe(new EventNotifier(viewFactory, this, clock));
        subscription.unsubscribe();
        LocalDate tomorrow = clock.now().plusDays(1);
        Timber.d("schedule next alarm using date %s", tomorrow);

        Observable<NotificationTime> nextAlarmObservable = repo.notificationTimeForFirstUpcomingEvent(tomorrow);
        nextAlarmObservable.subscribe(new AlarmGenerator(this));

    }

}
