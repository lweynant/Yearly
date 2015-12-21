package com.lweynant.yearly.model;

import com.google.gson.InstanceCreator;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import java.lang.reflect.Type;

public class EventInstanceCreator implements InstanceCreator<Event> {
    private final IClock clock;
    private final IUniqueIdGenerator uniqueIdGenerator;

    public EventInstanceCreator(IClock clock, IUniqueIdGenerator uniqueIdGenerator){
        this.clock = clock;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }
    @Override
    public Event createInstance(Type type) {
        return new Event("", Date.JANUARY, 1, clock, uniqueIdGenerator);
    }
}