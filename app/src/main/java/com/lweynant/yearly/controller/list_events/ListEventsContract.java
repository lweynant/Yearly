package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.IEvent;

import java.util.List;

public interface ListEventsContract {
    interface ActivityView {

        void showEventAdded(IEvent event);

        void showAddNewBirthdayUI();

        void showAddNewEventUI();

        void showEventDetailsUI(IEvent event);
    }
    interface FragmentView {


        void setProgressIndicator(boolean enable);

        void showEvents(List<IEvent> events);
    }
    interface UserActionsListener {

        void setFragmentView(FragmentView fragmentView);

        void setActivityView(ActivityView activityView);

        void removeEvent(IEvent event);

        void openEventDetails(IEvent requestedEvent);

        void loadEvents(boolean forceUpdate);

        void cancelLoadingEvents();

        void addNewBirthday();

        void addNewEvent();

    }
}
