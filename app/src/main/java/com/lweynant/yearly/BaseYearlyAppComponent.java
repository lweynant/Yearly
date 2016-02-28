package com.lweynant.yearly;

import com.lweynant.yearly.controller.add_event.AddBirthdayActivity;
import com.lweynant.yearly.controller.add_event.AddBirthdayActivityFragment;
import com.lweynant.yearly.controller.add_event.AddEventActivity;
import com.lweynant.yearly.controller.add_event.AddEventActivityFragment;
import com.lweynant.yearly.controller.list_events.ListEventsActivity;
import com.lweynant.yearly.controller.list_events.ListEventsActivityFragment;
import com.lweynant.yearly.controller.show_event.ShowBirthdayActivity;


public interface BaseYearlyAppComponent {
    void inject(YearlyApp app);

    void inject(ListEventsActivity listEventsActivity);

    void inject(ListEventsActivityFragment listEventsActivityFragment);

    void inject(AddBirthdayActivityFragment addBirthdayActivityFragment);

    void inject(EventNotificationService eventNotificationService);

    void inject(BootReceiver bootReceiver);

    void inject(AddEventActivityFragment addEventActivityFragment);

    void inject(ShowBirthdayActivity showBirthdayActivity);
}
