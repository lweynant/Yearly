package com.lweynant.yearly;

import com.lweynant.yearly.controller.AddBirthdayActivityFragment;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.controller.EventsActivityFragment;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.util.IClock;


public interface YearlyAppComponent {
    void inject(YearlyApp app);

    void inject(EventsActivity eventsActivity);

    void inject(EventsActivityFragment eventsActivityFragment);

    IClock clock();

    void inject(AddBirthdayActivityFragment addBirthdayActivityFragment);

    EventRepo eventRepo();
}
