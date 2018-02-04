package com.lweynant.yearly.platform;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.R;

import org.joda.time.LocalDate;

import timber.log.Timber;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.CATEGORY_REMINDER;

public class EventNotification implements IEventNotification {
    private final Context context;
    public static final String LAST_NOTIFICATION = "com.lweynant.last_notification";
    private static final String NOTIFICATION_CHANNEL_ID = "com.lweynant.yearly.events";
    private IPreferences preferences;
    private IClock clock;

    public EventNotification(Context context, IPreferences preferences, IClock clock) {
        Timber.d("create EventNotification instance");
        this.clock = clock;
        this.preferences = preferences;
        this.context = context;
    }

    @Override public void notify(int id, Intent intent,  IEventNotificationText notifText) {
        Timber.d("notify: sending notification for %s using id %d", notifText.getText(), id);

        NotificationManager nm = getNotificationManager();
        registerNotificationChannel(nm);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_cake_white_48dp);
        String title = notifText.getTitle();
        String subTitle = notifText.getText();
        builder.setContentTitle(title);
        builder.setContentText(subTitle);
        builder.setAutoCancel(true);
        builder.setCategory(CATEGORY_REMINDER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        nm.notify(id, builder.build());
        LocalDate now = clock.now();
        String time = String.format("%02d:%02d:%02d, %s", clock.hour(), clock.minutes(), clock.seconds(), now.toString());

        preferences.setStringValue(LAST_NOTIFICATION, time);
    }

    private void registerNotificationChannel(NotificationManager nm) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (nm.getNotificationChannel(NOTIFICATION_CHANNEL_ID) != null) {
//                return;
//            }
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Birthday Notifications", NotificationManager.IMPORTANCE_DEFAULT);

            // Configure the notification channel.
            notificationChannel.setDescription("Events");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.MAGENTA);
            notificationChannel.enableVibration(false);
            nm.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override public void cancel(int id) {
        NotificationManager nm = getNotificationManager();
        nm.cancel(id);
    }
}
