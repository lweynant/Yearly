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
    private static EventRepo instance = null;
    private int startDay = 1;
    private @Date.Month int startMonth = Date.JANUARY;
    private int nbrOfDaysForUpcomingEvents;

    public EventRepo(IClock clock){
        this.clock = clock;
    }

    public EventRepo add(IEvent event) {
        events.add(event);
        Collections.sort(events, new Comparator<IEvent>() {
            @Override
            public int compare(IEvent lhs, IEvent rhs) {
                if (lhs.getMonth() == rhs.getMonth()) {
                    if (lhs.getDay() < rhs.getDay() ) return -1;
                    else if (lhs.getDay() == rhs.getDay()) return 0;
                    else return 1;
                } else if (lhs.getMonth() < rhs.getMonth()) {
                    return -1;
                }
                return 1;
            }
        });
        return this;
    }

    public List<IEvent> getEvents() {
        List<IEvent> sortedEvents = new ArrayList<>();
        int start;
        for (start = 0; start < events.size(); start++){
            IEvent e = events.get(start);
            if (e.getMonth() == startMonth){
                if (e.getDay() >= startDay){
                    break;
                }
            }
            else if (e.getMonth() > startMonth){
                break;
            }
        }
        for (int i = 0; i < events.size(); i++){
            sortedEvents.add(events.get(start%events.size()));
            start++;
        }
        return sortedEvents;
    }



    public void sortFrom(@Date.Month int month, int day) {
        Timber.d("sort on %d/%d", day, month);
        startDay = day;
        startMonth = month;
    }

    public List<IEvent> getUpcomingEvents() {
        List<IEvent> sorted = getEvents();
        List<IEvent> upcoming = new ArrayList<>();
        if (sorted.size() > 0){
            IEvent upcomingEvent = sorted.get(0);
            upcoming.add(upcomingEvent);
            for(int i = 1; i < sorted.size();i++){
                if (sorted.get(i).getMonth() == upcomingEvent.getMonth()
                        && sorted.get(i).getDay()==upcomingEvent.getDay()){
                    upcoming.add(sorted.get(i));
                }
                else{
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
