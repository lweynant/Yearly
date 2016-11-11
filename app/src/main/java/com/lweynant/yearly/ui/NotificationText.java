package com.lweynant.yearly.ui;

import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotificationText;
import com.lweynant.yearly.utils.CaseFormat;

import org.joda.time.Days;
import org.joda.time.LocalDate;

public abstract class NotificationText  implements IEventNotificationText {
    protected final IClock clock;
    protected final IEvent event;
    protected final IEventStringResource stringResource;

    public NotificationText(IEvent event, IEventStringResource rstring, IClock clock) {
        this.event = event;
        this.stringResource = rstring;
        this.clock = clock;
    }

    public String getText() {
        LocalDate eventDate = event.getDate();
        LocalDate now = clock.now();
        String subTitle = when(eventDate, now);
        String text = subTitle + " " + eventDate.dayOfWeek().getAsText() + " " +
                eventDate.getDayOfMonth() + " "  + eventDate.monthOfYear().getAsText();
        return CaseFormat.capitalizeFirstLetter(text);
    }

    private String when(LocalDate eventDate, LocalDate now) {
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
                subTitle += " " +stringResource.getStringFromId(R.string.at);
            }
        }
        return subTitle;
    }

    public String getOneLiner() {
        return stringResource.getFormattedTitle(event) + " "  + CaseFormat.uncapitalizeFirstLetter(getText());
    }

}
