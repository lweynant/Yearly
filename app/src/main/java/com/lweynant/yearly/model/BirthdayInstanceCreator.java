package com.lweynant.yearly.model;

import com.google.gson.InstanceCreator;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import java.lang.reflect.Type;

public class BirthdayInstanceCreator implements InstanceCreator<Birthday> {
    private final IClock clock;
    private final IUniqueIdGenerator uniqueIdGenerator;

    public BirthdayInstanceCreator(IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.clock = clock;
        this.uniqueIdGenerator = uniqueIdGenerator;
    }

    @Override
    public Birthday createInstance(Type type) {
        return new Birthday("", Date.JANUARY, 1, clock, uniqueIdGenerator);
    }
}
