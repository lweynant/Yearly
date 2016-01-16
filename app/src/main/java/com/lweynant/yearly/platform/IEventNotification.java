package com.lweynant.yearly.platform;


import com.lweynant.yearly.model.IEvent;

public interface IEventNotification {
    void notify(int id, IEventNotificationText notifText);
}
