package com.lweynant.yearly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;


public class Birthday extends Event {

    public static final String KEY_YEAR_OF_BIRTH = "year";


    @Expose
    @SerializedName(KEY_YEAR_OF_BIRTH)
    private Integer yearOfBirth;

    public Birthday(String name, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        super(name, month, day, clock, uniqueIdGenerator);
        super.setNbrOfDaysForNotification(2);
    }

    public Birthday(String name, int yearOfBirth, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, month, day, clock, uniqueIdGenerator);
        this.yearOfBirth = yearOfBirth;
    }



}
