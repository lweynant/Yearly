package com.lweynant.yearly.utils;

import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.platform.IEventNotification;

public class RemoveAction {
    private ITransaction transaction;
    private IEventNotification eventNotification;

    public RemoveAction(ITransaction transaction, IEventNotification eventNotification) {
        this.transaction = transaction;
        this.eventNotification = eventNotification;
    }

    public void remove(IEvent event){
        transaction.remove(event).commit();
        eventNotification.cancel(event.getID());
    }
}
