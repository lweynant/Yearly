package com.lweynant.yearly.platform;


import android.content.Intent;

public interface IEventNotification {
    void notify(int id, Intent intent, IEventNotificationText notifText);
}
