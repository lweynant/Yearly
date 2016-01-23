package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.IEvent;

import java.util.Collection;
import java.util.List;

public interface ListEventsContract {
    interface View {

        void showEventDetailsUI(IEvent event);

        void setProgressIndicator(boolean enable);

        void showEvents(List<IEvent> events);
    }
    interface UserActionsListener {

        void setView(View view);

        void removeEvent(IEvent event);

        void openEventDetails(IEvent event);

        void loadEvents();
    }
}
