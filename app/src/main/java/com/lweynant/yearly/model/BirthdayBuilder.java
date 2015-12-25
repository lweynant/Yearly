package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

public class BirthdayBuilder {

    private final IClock clock;
    private final IUniqueIdGenerator uniquedIdGenerator;
    private String name;
    private
    @Date.Month
    int month;
    private int day;
    private int year;

    public BirthdayBuilder(IClock clock, IUniqueIdGenerator uniqueIdGenerator) {
        this.clock = clock;
        this.uniquedIdGenerator = uniqueIdGenerator;
    }

    public Birthday build() {
        if (validName(name) && validMonth(month) && validDay(day)) {
            if (validYear(year)) {
                return new Birthday(name, year, month, day, clock, uniquedIdGenerator);
            } else {
                return new Birthday(name, month, day, clock, uniquedIdGenerator);
            }
        }
        return null;
    }

    private boolean validYear(int year) {
        return year != 0;
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

    public void setName(String newName) {
        this.name = newName;
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
}
