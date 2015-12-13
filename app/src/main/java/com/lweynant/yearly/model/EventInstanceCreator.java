package com.lweynant.yearly.model;

import com.google.gson.InstanceCreator;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import java.lang.reflect.Type;

public class EventInstanceCreator implements InstanceCreator<Event> {
    private final IClock clock;
    private final IUUID iuuid;

    public EventInstanceCreator(IClock clock, IUUID iuuid){
        this.clock = clock;
        this.iuuid = iuuid;
    }
    @Override
    public Event createInstance(Type type) {
        return new Event(Date.JANUARY, 1, clock, iuuid);
    }
}