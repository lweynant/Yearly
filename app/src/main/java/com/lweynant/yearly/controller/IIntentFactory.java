package com.lweynant.yearly.controller;

import android.content.Intent;

import com.lweynant.yearly.model.IEvent;

public interface IIntentFactory {
    Intent createNotificationIntent(IEvent event);
}
