package com.lweynant.yearly;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.lweynant.yearly.controller.EventNotifier;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.AlarmGenerator;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventViewFactory;

import org.joda.time.LocalDate;

import javax.inject.Inject;

import rx.Observable;
import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class EventNotificationService extends IntentService {
    @Inject IClock clock;
    @Inject EventRepo repo;
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

        LocalDate tomorrow = clock.now().plusDays(1);
        Timber.d("schedule next alarm using date %s", tomorrow);

        alarmGenerator.generate(repo.getEvents(), tomorrow);

    }

}
