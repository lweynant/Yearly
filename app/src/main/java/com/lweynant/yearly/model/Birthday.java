package com.lweynant.yearly.model;

import android.os.Bundle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;


public class Birthday extends Event {

    public static final String KEY_LAST_NAME = "last_name";

    @Expose @SerializedName(KEY_LAST_NAME) private final String lastName;

    public Birthday(IEventID id, String name, String lastName, Integer year, @Date.Month int month, int day, IClock clock) {
        super(id, name, year, month, day, clock);
        super.setNbrOfDaysForNotification(2);
        this.lastName = lastName;
    }
    public Birthday(String name, String lastName, Integer year, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        super(name, year, month, day, clock, uniqueIdGenerator);
        super.setNbrOfDaysForNotification(2);
        this.lastName = lastName;
    }
    public Birthday(String name, String lastName,  @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, lastName, null, month, day, clock, uniqueIdGenerator);
    }

    public Birthday(String name, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, null, null, month, day, clock, uniqueIdGenerator);
    }

    public Birthday(String name, String lastName, int yearOfBirth, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, lastName, new Integer(yearOfBirth),  month, day, clock, uniqueIdGenerator);
    }

    public Birthday(String name, int yearOfBirth, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, null, yearOfBirth, month, day, clock, uniqueIdGenerator);
    }

    @Override public String toString() {
        if (!hasYearOfOrigin() && lastName == null) {
            return super.toString();
        } else {
            String date = getDate().toString("dd-MM");
            String name = getName();
            if (hasYearOfOrigin()) {
                date += "-" + getYearOfOrigin();
            }
            if (lastName != null) {
                name += " " + lastName;
            }
            return name + " - " + date;
        }

    }

    @Override public void archiveTo(Bundle bundle) {
        super.archiveTo(bundle);
        if (hasLastName()){
            bundle.putString(KEY_LAST_NAME, getLastName());
        }
    }

    private boolean hasLastName() {
        return lastName != null;
    }

    public String getLastName() {
        return lastName;
    }
}
