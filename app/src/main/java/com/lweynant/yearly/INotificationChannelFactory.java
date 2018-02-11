package com.lweynant.yearly;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.os.Build;
import android.support.annotation.RequiresApi;

interface INotificationChannelFactory {
    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannel createChannel(String id, String name, int importance);

    @RequiresApi(api = Build.VERSION_CODES.O)
    NotificationChannelGroup createGroup(String groupId, String name);
}
