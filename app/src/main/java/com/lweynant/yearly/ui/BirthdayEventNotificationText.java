package com.lweynant.yearly.ui;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.utils.CaseFormat;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public class BirthdayEventNotificationText implements IEventNotificationText {
    private final IClock clock;
    private final IEvent event;
    private final BirthdayStringResource stringResource;

    public BirthdayEventNotificationText(IEvent event, IStringResources rstring, IClock clock) {
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
            if (days == 2) {
                subTitle = stringResource.getStringFromId(R.string.day_after_tomorrow);
            }
            else {
                subTitle = String.format(stringResource.getStringFromId(R.string.in_x_days), days);
            }
        }
        return CaseFormat.capitalizeFirstLetter(subTitle + ", "
                + eventDate.dayOfWeek().getAsText() + " " + eventDate.getDayOfMonth() + " " + eventDate.monthOfYear().getAsText());
    }

    @Override
    public String getOneLiner() {
        return stringResource.getFormattedTitle(event) + " " + CaseFormat.uncapitalizeFirstLetter(getText());
    }


}
