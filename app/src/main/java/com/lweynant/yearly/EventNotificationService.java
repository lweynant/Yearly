package com.lweynant.yearly;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.controller.EventNotifier;
import com.lweynant.yearly.controller.AlarmGenerator;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class EventNotificationService extends IntentService {
    @Inject IClock clock;
    @Inject IEventRepo repo;
    @Inject AlarmGenerator alarmGenerator;
    @Inject EventNotifier eventNotifier;
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

    @Override public void onCreate() {
        super.onCreate();
        ((YearlyApp)getApplication()).getComponent().inject(this);
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

        eventNotifier.notify(repo.getEvents());

        LocalDate now = clock.now();
        int hour = clock.hour();
        Timber.d("schedule next alarm using date %s and hour %d", now, hour);

        alarmGenerator.generate(repo.getEvents(), now, hour);

    }

}
