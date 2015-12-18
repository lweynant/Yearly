package com.lweynant.yearly.ui;

import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.util.IClock;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class BirthdayEventNotificationText implements IEventNotificationText {
    private final IClock clock;
    private final IEvent event;
    private final BirthdayStringResource stringResource;

    public BirthdayEventNotificationText(IEvent event, IRString rstring, IClock clock) {
        this.stringResource = new BirthdayStringResource(rstring);
        this.clock = clock;
        this.event = event;
    }

    @Override
    public String getTitle() {
        return stringResource.getFormattedTitle(event);
    }

    @Override
    public String getText() {
        LocalDate eventDate = event.getDate();
        LocalDate now = clock.now();
        String subTitle;
        if (eventDate.isEqual(now)) {
            subTitle = stringResource.getStringFromId(R.string.today);
        } else if (eventDate.minusDays(1).isEqual(now)) {
            subTitle = stringResource.getStringFromId(R.string.tomorrow);
        } else {
            Days d = Days.daysBetween(now, eventDate);
            int days = d.getDays();
            subTitle = String.format(stringResource.getStringFromId(R.string.in_x_days), days);
        }
        return subTitle;
    }

    @Override
    public String getOneLiner() {
        return stringResource.getFormattedTitle(event) + " " + getText();
    }


}
