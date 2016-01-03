package com.lweynant.yearly.matcher;

import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.LocalDate;

public class IsBirthday extends TypeSafeMatcher<Birthday> {

    private static final int INVALID_YEAR = Integer.MIN_VALUE;
    private final String name;
    private final int year;
    private final @Date.Month int month;
    private final int day;
    private final String lastName;

    private IsBirthday(String name, int year, @Date.Month int month, int day) {
        this(name, null, year, month, day);
    }

    public IsBirthday(String name, String lastName, int year, int month, int day) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
        this.lastName = lastName;

    }

    @Factory
    public static <T> Matcher<Birthday> birthday(String name, @Date.Month int month, int day) {
        return new IsBirthday(name, INVALID_YEAR, month, day);
    }

    @Factory
    public static <T> Matcher<Birthday> birthday(String name, int year, @Date.Month int month, int day) {
        return new IsBirthday(name, year, month, day);
    }

    @Factory
    public static <T> Matcher<Birthday> birthday(String name, String lastName, @Date.Month int month, int day) {
        return new IsBirthday(name, lastName, INVALID_YEAR, month, day);
    }

    @Factory
    public static <T> Matcher<Birthday> birthday(String name, String lastName, int year, @Date.Month int month, int day) {
        return new IsBirthday(name, lastName, year, month, day);
    }

    @Override
    protected boolean matchesSafely(Birthday actual) {
        LocalDate actualDate = actual.getDate();
        if (actual.getName().equals(name) && sameLastName(actual) && actualDate.getMonthOfYear() == month && actualDate.getDayOfMonth() == day) {
            if (year == INVALID_YEAR) {
                return true;
            } else {
                return actual.getYear() == year;
            }
        } else {
            return false;
        }
    }

    private boolean sameLastName(Birthday actual) {
        if (actual.getLastName() == null) return lastName == null;
        else return actual.getLastName().equals(lastName);
    }

    @Override
    public void describeTo(Description description) {
        if (year == INVALID_YEAR) {
            description.appendText(String.format("<%s %s - %02d-%02d>", name, getValidLastName(lastName), day, month));
        } else {
            description.appendText(String.format("<%s %s - %02d-%02d-%04d>", name, getValidLastName(lastName), day, month, year));
        }
    }

    private String getValidLastName(String lastName) {
        return lastName == null ? "" : lastName;
    }
}
