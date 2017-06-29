package com.lweynant.yearly;

import com.lweynant.yearly.controller.add_event.AddBirthdayActivityFragment;
import com.lweynant.yearly.controller.add_event.AddEventActivityFragment;
import com.lweynant.yearly.controller.archive.ArchiveActivity;
import com.lweynant.yearly.controller.archive.RestoreActivity;
import com.lweynant.yearly.controller.list_events.ListEventsActivity;
import com.lweynant.yearly.controller.list_events.ListEventsActivityFragment;
import com.lweynant.yearly.controller.list_events.ListBirthdaysActivity;
import com.lweynant.yearly.controller.settings.SettingsActivity;
import com.lweynant.yearly.controller.show_event.ShowBirthdayActivity;
import com.lweynant.yearly.controller.show_event.ShowBirthdayFragment;


public interface BaseYearlyAppComponent {
    void inject(YearlyApp app);

    void inject(ListEventsActivity listEventsActivity);

    void inject(ListEventsActivityFragment listEventsActivityFragment);

    void inject(AddBirthdayActivityFragment addBirthdayActivityFragment);

    void inject(EventNotificationService eventNotificationService);

    void inject(BootReceiver bootReceiver);

    void inject(AddEventActivityFragment addEventActivityFragment);

    void inject(ShowBirthdayFragment showBirthdayFragment);

    void inject(ShowBirthdayActivity showBirthdayActivity);

    void inject(ListBirthdaysActivity listBirthdaysActivity);

    void inject(SettingsActivity settingsActivity);

    void inject(ArchiveActivity archiveActivity);

    void inject(RestoreActivity restoreActivity);
}
