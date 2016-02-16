package com.lweynant.yearly.matcher;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.LocalDate;

public class IsEvent extends TypeSafeMatcher<IEvent> {

    private static final int INVALID_YEAR = Integer.MIN_VALUE;
    private final String name;
    private final int year;
    private final @Date.Month int month;
    private final int day;
    private final Integer id;
    private final String stringId;

    private IsEvent(String name, int year, @Date.Month int month, int day) {
        this(null, null, name, year, month, day);
    }

    public IsEvent(String stringID, Integer id, String name, int year, int month, int day) {
        this.id = id;
        this.name = name;
        this.stringId = stringID;
        this.year = year;
        this.month = month;
        this.day = day;
    }


    @Factory
    public static <T> Matcher<IEvent> event(String stringID, int id, String name, @Date.Month int month, int day) {
        return new IsEvent(stringID, id, name, INVALID_YEAR, month, day);
    }
    @Factory
    public static <T> Matcher<IEvent> event(String name, @Date.Month int month, int day) {
        return new IsEvent(name, INVALID_YEAR, month, day);
    }

    @Factory
    public static <T> Matcher<IEvent> event(String name, int year, @Date.Month int month, int day) {
        return new IsEvent(name, year, month, day);
    }

    @Override
    protected boolean matchesSafely(IEvent actual) {
        LocalDate actualDate = actual.getDate();
        if (id != null) {
            if (actual.getID() != id || actual.getStringID() != stringId) return false;
        }
        //noinspection ResourceType
        if (actual.getName().equals(name) && actualDate.getMonthOfYear() == month && actualDate.getDayOfMonth() == day) {
            if (year == INVALID_YEAR) {
                return true;
            } else {
                return actual.hasYearOfOrigin() && actual.getYearOfOrigin() == year;
            }
        } else {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        String prefix = "";
        if (id != null) {
            prefix += "(" + stringId + ", "+ Integer.toString(id) + ")";
        }
        if (year == INVALID_YEAR) {
            description.appendText(String.format("%s<%s - %02d-%02d>", prefix, name, day, month));
        } else {
            description.appendText(String.format("%s<%s - %02d-%02d-%04d>", prefix, name, day, month, year));
        }
    }

}