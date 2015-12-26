package com.lweynant.yearly.model;

import android.os.Bundle;

import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

public class BirthdayBuilder {

    public static final String KEY_NAME = "name";
    public static final String KEY_YEAR = "year";
    public static final String KEY_MONTH = "month";
    public static final String KEY_DAY = "day";
    public static final String KEY_LAST_NAME = "last_name";
    private static final int INVALID_YEAR = 0;
    private final IClock clock;
    private final IUniqueIdGenerator uniquedIdGenerator;
    private String name;
    private
    @Date.Month
    int month;
    private int day;
    private int year;
    private String lastName;

    public BirthdayBuilder(IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.clock = clock;
        this.uniquedIdGenerator = uniqueIdGenerator;
    }

    public Birthday build() {
        if (validName(name) && validMonth(month) && validDay(day)) {
            if (validYear(year)) {
                return new Birthday(name, lastName, year, month, day, clock, uniquedIdGenerator);
            } else {
                return new Birthday(name, lastName, month, day, clock, uniquedIdGenerator);
            }
        }
        return null;
    }

    private boolean validYear(int year) {
        return year != INVALID_YEAR;
    }

    private boolean validDay(int dayOfMonth) {
        return dayOfMonth > 0 && dayOfMonth <= 31;
    }

    private boolean validMonth(int month) {
        return month > 0 && month <= 12;
    }

    private boolean validName(String name) {
        return name != null && !name.isEmpty();
    }

    public BirthdayBuilder setName(String newName) {
        this.name = newName;
        return this;
    }

    public BirthdayBuilder setDay(int day) {
        this.day = day;
        return this;
    }

    public BirthdayBuilder setMonth(@Date.Month int month) {
        this.month = month;
        return this;
    }

    public BirthdayBuilder setYear(int year) {
        this.year = year;
        return this;
    }

    public void archiveTo(Bundle bundle) {
        archiveString(name, KEY_NAME, bundle);
        archiveString(lastName, KEY_LAST_NAME, bundle);
        if (validYear(year)){
            bundle.putInt(KEY_YEAR, year);
        }
        else {
            bundle.remove(KEY_YEAR);
        }
        if (validMonth(month)){
            bundle.putInt(KEY_MONTH, month);
        }
        else {
            bundle.remove(KEY_MONTH);
        }
        if (validDay(day)){
            bundle.putInt(KEY_DAY, day);
        }
        else {
            bundle.remove(KEY_DAY);
        }
    }

    private void archiveString(String string, String key, Bundle bundle) {
        if (validName(string)){
            bundle.putString(key, string);
        }
        else {
            bundle.remove(key);
        }
    }

    public BirthdayBuilder setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public BirthdayBuilder set(Bundle bundle) {
        if (bundle.containsKey(KEY_NAME)){
            name = bundle.getString(KEY_NAME);
        }
        if (bundle.containsKey(KEY_LAST_NAME)){
            lastName = bundle.getString(KEY_LAST_NAME);
        }
        if (bundle.containsKey(KEY_YEAR)){
            year = bundle.getInt(KEY_YEAR);
        }
        if (bundle.containsKey(KEY_MONTH)){
            //noinspection ResourceType
            month = bundle.getInt(KEY_MONTH);
        }
        if (bundle.containsKey(KEY_DAY)){
            day = bundle.getInt(KEY_DAY);
        }
        return this;
    }

    public BirthdayBuilder clearYear() {
        year = INVALID_YEAR;
        return this;
    }
}
