package com.lweynant.yearly;

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
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.platform.IPreferences;

import org.joda.time.LocalDate;

import timber.log.Timber;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.support.v4.app.NotificationCompat.CATEGORY_REMINDER;

public class EventNotification implements IEventNotification {
    private final Context context;
    public static final String LAST_NOTIFICATION = "com.lweynant.last_notification";
    private final IDateFormatter dateFormatter;
    private IPreferences preferences;
    private IClock clock;

    public EventNotification(Context context, IPreferences preferences, IDateFormatter dateFormatter, IClock clock) {
        Timber.d("create EventNotification instance");
        this.clock = clock;
        this.preferences = preferences;
        this.context = context;
        this.dateFormatter = dateFormatter;
    }

    @Override public void notify(int id, Intent intent,  IEventNotificationText notifText) {
        Timber.d("notify: sending notification for %s using id %d", notifText.getText(), id);

        NotificationManager nm = getNotificationManager();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationChannels.NOTIFICATION_CHANNEL_BIRTHDAY);
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
        String time = dateFormatter.format(clock);
        preferences.setStringValue(LAST_NOTIFICATION, time);
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
    }

    @Override public void cancel(int id) {
        NotificationManager nm = getNotificationManager();
        nm.cancel(id);
    }
}
