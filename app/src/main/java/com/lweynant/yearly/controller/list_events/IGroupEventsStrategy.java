package com.lweynant.yearly.controller.list_events;

import android.support.annotation.NonNull;

import com.lweynant.yearly.model.IEvent;

public interface IGroupEventsStrategy {
    @NonNull ListEventsContract.ListItem createListItem(IEvent event);

    @NonNull ListEventsContract.ListItem createListItem(String group);

    String group(IEvent e);
}
