package com.lweynant.yearly;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IEvent;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.List;

import timber.log.Timber;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class EventNotificationService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NOTIFY = "com.lweynant.yearly.action.ACTION-NOTIFY";

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
    public EventNotificationService() {
        super("EventNotificationService");
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
        YearlyApp app = (YearlyApp)getApplication();
        EventRepo repo = app.getRepo();
        LocalDate now = LocalDate.now();
        repo.sortFrom(now.getMonthOfYear(), now.getDayOfMonth());
        List<IEvent> upcomingEvents = repo.getUpcomingEvents();

        if (upcomingEvents.isEmpty()) {
            Timber.e("no upcoming events ...");
            return;
        }
        int id = 0;
        for (IEvent event :upcomingEvents){
            Timber.d("sending notification for %s", event.getTitle());
            LocalDate eventDate = new LocalDate(now.getYear(), event.getMonth(), event.getDay());
            if (eventDate.isBefore(now)){
                eventDate = eventDate.plusYears(1);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_event_note_white_48dp);
            String title = event.getTitle() + " ";
            if (eventDate.isEqual(now))
            {
                title += getResources().getString(R.string.today);
            }
            else if (eventDate.minusDays(1).isEqual(now)){
                title += getResources().getString(R.string.tomorrow);
            }
            else {
                Days d = Days.daysBetween(now, eventDate);
                int days = d.getDays();
                title += "in " + days + "days";
            }
            builder.setContentTitle(title);
            builder.setAutoCancel(true);
            Intent intent = new Intent(this, EventsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            nm.notify(id++, builder.build());
        }
        AlarmGenerator alarmGenerator = new AlarmGenerator(this, repo);
        LocalDate tomorrow = now.plusDays(1);
        Timber.d("schedule next alarm using date %s", tomorrow);
        alarmGenerator.startAlarm(tomorrow);

    }

}
