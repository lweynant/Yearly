package com.lweynant.yearly;

import com.lweynant.yearly.controller.add_event.AddBirthdayActivity;
import com.lweynant.yearly.controller.add_event.AddBirthdayActivityFragment;
import com.lweynant.yearly.controller.DateSelector;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.controller.EventsActivityFragment;


public interface BaseYearlyAppComponent {
    void inject(YearlyApp app);

    void inject(EventsActivity eventsActivity);

    void inject(EventsActivityFragment eventsActivityFragment);

    void inject(AddBirthdayActivityFragment addBirthdayActivityFragment);

    void inject(EventNotificationService eventNotificationService);

    void inject(BootReceiver bootReceiver);

    void inject(AddBirthdayActivity addBirthdayActivity);

}
