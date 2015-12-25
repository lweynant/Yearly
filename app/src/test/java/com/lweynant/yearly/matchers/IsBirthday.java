package com.lweynant.yearly.matchers;

import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.TypeSafeMatcher;
import org.joda.time.LocalDate;

public class IsBirthday extends TypeSafeMatcher<Birthday> {

    private static final int INVALID_YEAR = Integer.MIN_VALUE;
    private final String name;
    private final int year;
    private final @Date.Month int month;
    private final int day;

    private IsBirthday(String name, int year, @Date.Month int month, int day) {
        this.name = name;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Override
    protected boolean matchesSafely(Birthday actual) {
        LocalDate actualDate = actual.getDate();
        if (actual.getName().equals(name) && actualDate.getMonthOfYear() == month && actualDate.getDayOfMonth() == day) {
            if (year == INVALID_YEAR) {
                return true;
            } else {
                return actual.getYear() == year;
            }
        }
        else {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        if (year == INVALID_YEAR) {
            description.appendText(String.format("<%s - %02d-%02d>", name, day, month));
        } else {
            description.appendText(String.format("<%s - %02d-%02d-%04d>", name, day, month, year));
        }
    }

    @Factory
    public static <T> Matcher<Birthday> birthday(String name, @Date.Month int month, int day) {
        return new IsBirthday(name, INVALID_YEAR, month, day);
    }

    @Factory
    public static <T> Matcher<Birthday> birthday(String name, int year, @Date.Month int month, int day) {
        return new IsBirthday(name, year, month, day);
    }
}
