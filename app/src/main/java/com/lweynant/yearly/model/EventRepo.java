package com.lweynant.yearly.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EventRepo {
    private List<IEvent> events = new ArrayList<>();
    private static EventRepo instance = null;
    private int startDay = 1;
    private @Date.Month int startMonth = Date.JANUARY;
    private EventRepo(){

    }

    public static EventRepo getInstance(){
        if (instance == null) {
            instance = new EventRepo();
        }
        return instance;
    }
    public EventRepo add(IEvent event) {
        events.add(event);
        Collections.sort(events, new Comparator<IEvent>() {
            @Override
            public int compare(IEvent lhs, IEvent rhs) {
                if (lhs.getMonth() == rhs.getMonth()) {
                    return Integer.compare(lhs.getDay(), rhs.getDay());
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


    public static void deleteInstance() {
        instance = null;
    }

    public void sortFrom(int day, @Date.Month int month) {
        startDay = day;
        startMonth = month;
    }
}
