package com.lweynant.yearly.ui;

import com.lweynant.yearly.model.IEvent;

public interface IEventStringResource {
    String getFormattedTitle(IEvent event);

    String getStringFromId(int id);
}
