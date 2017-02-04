package com.lweynant.yearly.controller.list_events;

import android.support.annotation.NonNull;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;

public class GroupEventsByDate implements IGroupEventsStrategy {
    private final IClock clock;
    private final IStringResources stringResources;
    private final String[] nearFuture;

    public GroupEventsByDate(IClock clock, IStringResources stringResources) {
        this.clock = clock;
        this.stringResources = stringResources;
        this.nearFuture = stringResources.getStringArray(R.array.near_future);
    }

    @Override @NonNull public ListEventsContract.ListItem createListItem(IEvent event) {
        return new ListEventsContract.ListItem(event);
    }

    @Override @NonNull public ListEventsContract.ListItem createListItem(String group) {
        return new ListEventsContract.ListItem(group);
    }

    @Override public String group(IEvent e) {
        LocalDate date = e.getDate();
        LocalDate now = clock.now();
        if (nearFuture.length > 0 && now.isEqual(date)) {
            return nearFuture[0];
        }
        else if (nearFuture.length > 1 && now.plusDays(1).isEqual(date)){
            return stringResources.getStringArray(R.array.near_future)[1];
        }
        else if (nearFuture.length > 2 && now.plusDays(2).isEqual(date)) {
            return stringResources.getStringArray(R.array.near_future)[2];
        }
        else if ((date.getMonthOfYear() == now.getMonthOfYear()) && date.getYear() > now.getYear()) {
            String string = stringResources.getStringArray(R.array.months)[e.getDate().getMonthOfYear()];
            return string + " " + Integer.toString(date.getYear());
        }
        else {
            return stringResources.getStringArray(R.array.months)[e.getDate().getMonthOfYear()];
        }

    }

}
