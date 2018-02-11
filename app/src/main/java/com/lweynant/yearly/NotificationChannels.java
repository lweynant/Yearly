package com.lweynant.yearly;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;


public class NotificationChannels {
    public static final String NOTIFICATION_CHANNEL_BIRTHDAY = "com.lweynant.yearly.notification_channel_birthday";
    public static final String NOTIFICATION_CHANNEL_DAY_BEFORE_BIRTHDAY = "com.lweynant.yearly.notification.channel_day_before_birthday";
    public static final String NOTIFICATION_CHANNEL_2DAYS_BEFORE_BIRTHDAY = "com.lweynant.yearly.notification.channel_2days_before_birthday";
    public static final String DEFAULT_GROUP_ID = "com.lweynant.yearly.notification_group_default";
    private INotificationChannelFactory channelFactory;
    private NotificationManager notificationManager;
    private IStringResources stringResources;

    public NotificationChannels(INotificationChannelFactory channelFactory,
                                NotificationManager notificationManager,
                                IStringResources stringResources) {
        this.channelFactory = channelFactory;
        this.notificationManager = notificationManager;
        this.stringResources = stringResources;
    }

    public void registerNotificationChannels(String groupId, String groupName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerGroup(createGroup(groupId, groupName));
            registerChannel(createBirthdayChannel(), groupId);
            registerChannel(createDayBeforeBirthdayChannel(), groupId);
            registerChannel(create2DaysBeforeBirthdayChannel(), groupId);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerGroup(NotificationChannelGroup group) {
        notificationManager.createNotificationChannelGroup(group);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannelGroup createGroup(String groupId, String groupName) {
        return channelFactory.createGroup(groupId, groupName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void registerChannel(NotificationChannel channel, String groupId) {
        channel.setGroup(groupId);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createBirthdayChannel() {
        NotificationChannel channel =
                channelFactory.createChannel(NOTIFICATION_CHANNEL_BIRTHDAY,
                        stringResources.getString(R.string.notification_channel_birthday_name), NotificationManager.IMPORTANCE_DEFAULT);
        // Configure the notification channel.
        channel.setDescription(stringResources.getString(R.string.notification_channel_birthday_desc));
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel createDayBeforeBirthdayChannel() {
        NotificationChannel channel =
                channelFactory.createChannel(NOTIFICATION_CHANNEL_DAY_BEFORE_BIRTHDAY,
                        stringResources.getString(R.string.notification_channel_day_before_birthday_name), NotificationManager.IMPORTANCE_DEFAULT);

        // Configure the notification channel.
        channel.setDescription(stringResources.getString(R.string.notification_channel_day_before_birthday_desc));
        channel.enableLights(true);
        channel.setLightColor(Color.YELLOW);
        channel.enableVibration(true);
        return channel;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel create2DaysBeforeBirthdayChannel() {
        NotificationChannel channel =
                channelFactory.createChannel(NOTIFICATION_CHANNEL_2DAYS_BEFORE_BIRTHDAY,
                        stringResources.getString(R.string.notification_channel_2days_before_birthday_name), NotificationManager.IMPORTANCE_LOW);

        // Configure the notification channel.
        channel.setDescription(stringResources.getString(R.string.notification_channel_2days_before_birthday_desc));
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.enableVibration(true);
        return channel;
    }

}
