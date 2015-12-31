package com.lweynant.yearly;

import com.lweynant.yearly.controller.AddBirthdayActivityFragment;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.controller.EventsActivityFragment;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.util.IClock;


public interface BaseYearlyAppComponent {
    void inject(YearlyApp app);

    void inject(EventsActivity eventsActivity);

    void inject(EventsActivityFragment eventsActivityFragment);

    void inject(AddBirthdayActivityFragment addBirthdayActivityFragment);

    void inject(EventNotificationService eventNotificationService);

    void inject(BootReceiver bootReceiver);
}
