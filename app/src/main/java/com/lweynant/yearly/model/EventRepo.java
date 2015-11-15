package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import timber.log.Timber;

public class EventRepo {
    private final IClock clock;
    private List<IEvent> events = new ArrayList<>();
    private LocalDate startDate;
    private int nbrOfDaysForUpcomingEvents;

    public EventRepo(IClock clock) {
        this.clock = clock;
        this.startDate = clock.now();
    }

    public EventRepo add(IEvent event) {
        events.add(event);
        Collections.sort(events, new Comparator<IEvent>() {
            @Override
            public int compare(IEvent lhs, IEvent rhs) {
                return lhs.getDate().compareTo(rhs.getDate());
            }
        });
        return this;
    }

    public List<IEvent> getEvents() {
        List<IEvent> sortedEvents = new ArrayList<>();
        int start;
        for (start = 0; start < events.size(); start++) {
            IEvent e = events.get(start);
            LocalDate eventDate = e.getDate();
            if (eventDate.isEqual(startDate) || eventDate.isAfter(startDate)) break;
        }
        for (int i = 0; i < events.size(); i++) {
            sortedEvents.add(events.get(start % events.size()));
            start++;
        }
        return sortedEvents;
    }


    public void sortFrom(@Date.Month int month, int day) {
        Timber.d("sort on %d/%d", day, month);
        startDate = new LocalDate(clock.now().getYear(), month, day);
    }

    public List<IEvent> getUpcomingEvents() {
        List<IEvent> sorted = getEvents();
        List<IEvent> upcoming = new ArrayList<>();
        if (sorted.size() > 0) {
            IEvent upcomingEvent = sorted.get(0);
            upcoming.add(upcomingEvent);
            for (int i = 1; i < sorted.size(); i++) {
                if (sorted.get(i).getDate().isEqual(upcomingEvent.getDate())) {
                    upcoming.add(sorted.get(i));
                } else {
                    break;
                }

            }
        }
        return upcoming;
    }


    public void setNbrOfDaysForUpcomingEvents(int nbrOfDaysForUpcomingEvents) {
        this.nbrOfDaysForUpcomingEvents = nbrOfDaysForUpcomingEvents;
    }
}
