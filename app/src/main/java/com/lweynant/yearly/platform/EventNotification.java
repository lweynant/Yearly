package com.lweynant.yearly.platform;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.lweynant.yearly.R;

import timber.log.Timber;

public class EventNotification implements IEventNotification {
    private final Context context;

    public EventNotification(Context context) {
        Timber.d("create EventNotification instance");
        this.context = context;
    }

    @Override public void notify(int id, Intent intent,  IEventNotificationText notifText) {
        Timber.d("notify: sending notification for %s using id %d", notifText.getText(), id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_cake_white_48dp);
        String title = notifText.getTitle();
        String subTitle = notifText.getText();
        builder.setContentTitle(title);
        builder.setContentText(subTitle);
        builder.setAutoCancel(true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager nm = getNotificationManager();
        nm.notify(id, builder.build());
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override public void cancel(int id) {
        NotificationManager nm = getNotificationManager();
        nm.cancel(id);
    }
}
