package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import java.util.List;

import rx.Observable;

public interface ListEventsContract {
    class ListItem {
        private String separator;
        private IEvent event;

        public ListItem(String separator){
            this.separator = separator;
        }
        public ListItem(IEvent event) {
            this.event = event;
        }
        public boolean isSeparator() {
            return separator != null;
        }
        public String getSeparator() {
            return separator;
        }
        public boolean isEvent() {
            return event != null;
        }
        public IEvent getEvent() {
            return event;
        }
    }
    interface ActivityView {

        void showAddNewBirthdayUI();

        void showAddNewEventUI();

        void showEventDetailsUI(IEvent event);
    }
    interface FragmentView {


        void setProgressIndicator(boolean enable);

        void showListItems(Observable<ListItem> items);
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
