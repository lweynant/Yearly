package com.lweynant.yearly.ui;

import com.lweynant.yearly.R;
import com.lweynant.yearly.utils.CaseFormat;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class BirthdayNotificationText extends NotificationText {

    public BirthdayNotificationText(IEvent event, IEventStringResource rstring, IClock clock) {
        super(event, rstring, clock);
    }

    @Override
    public String getTitle() {
        return CaseFormat.capitalizeFirstLetter(stringResource.getFormattedTitle(event));
    }


}
