package com.lweynant.yearly.model;

import com.google.gson.InstanceCreator;
import com.lweynant.yearly.IRString;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import java.lang.reflect.Type;

public class BirthdayInstanceCreator implements InstanceCreator<Birthday> {
    private final IClock clock;
    private final IUUID iuuid;
    private final IRString rstring;

    public BirthdayInstanceCreator(IClock clock, IUUID iuuid, IRString rstring){
        this.clock = clock;
        this.iuuid = iuuid;
        this.rstring = rstring;
    }
    @Override
    public Birthday createInstance(Type type) {
        return new Birthday("", Date.JANUARY, 1, clock, iuuid, rstring);
    }
}
