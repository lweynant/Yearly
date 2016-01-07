package com.lweynant.yearly.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;


public class Birthday extends Event {

    public static final String KEY_YEAR_OF_BIRTH = "year";
    public static final String KEY_LAST_NAME = "last_name";

    @Expose @SerializedName(KEY_LAST_NAME) private final String lastName;

    @Expose @SerializedName(KEY_YEAR_OF_BIRTH) private Integer yearOfBirth;

    public Birthday(String name, String lastName, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        super(name, month, day, clock, uniqueIdGenerator);
        super.setNbrOfDaysForNotification(2);
        this.lastName = lastName;
    }

    public Birthday(String name, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, null, month, day, clock, uniqueIdGenerator);
    }

    public Birthday(String name, String lastName, int yearOfBirth, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, lastName, month, day, clock, uniqueIdGenerator);
        this.yearOfBirth = yearOfBirth;
    }

    public Birthday(String name, int yearOfBirth, @Date.Month int month, int day, IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this(name, null, yearOfBirth, month, day, clock, uniqueIdGenerator);
    }

    public int getYear() {
        return yearOfBirth;
    }

    @Override
    public String toString() {
        if (yearOfBirth == null && lastName == null) {
            return super.toString();
        } else {
            String date = getDate().toString("dd-MM");
            String name = getName();
            if (yearOfBirth != null) {
                date += "-" + yearOfBirth;
            }
            if (lastName != null) {
                name += " " + lastName;
            }
            return name + " - " + date;
        }

    }

    public String getLastName() {
        return lastName;
    }
}
