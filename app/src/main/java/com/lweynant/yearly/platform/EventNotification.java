package com.lweynant.yearly.platform;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lweynant.yearly.R;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.ui.EventViewFactory;
import com.lweynant.yearly.ui.IEventNotificationText;

import org.joda.time.LocalDate;

import timber.log.Timber;

public class EventNotification implements IEventNotification {
    private final Context context;
    private final IClock clock;

    public EventNotification(Context context, IClock clock) {
        Timber.d("create EventNotification instance");
        this.context = context;
        this.clock = clock;
    }

    @Override public void notify(IEvent event, IEventNotificationText notifText) {
        Timber.d("notify: sending notification for %s using id %d", event.toString(), event.getID());
        LocalDate now = clock.now();
        LocalDate eventDate = event.getDate();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_cake_white_48dp);
        String title = notifText.getTitle();
        String subTitle = notifText.getText();
        builder.setContentTitle(title);
        builder.setContentText(subTitle);
        builder.setAutoCancel(true);
        Intent intent = new Intent(context, EventsActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(event.getID(), builder.build());
    }
}
