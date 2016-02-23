package com.lweynant.yearly.model;


import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BirthdayTest {

    private static final String BIRTHDAY_TITLE = "%1$s's birthday";
    @Mock IClock clock;
    @Mock IUniqueIdGenerator uniqueIdGenerator;

    @Before public void setUp() {
        when(clock.now()).thenReturn(new LocalDate(2015, 8, 9));
    }

    @Test public void getTitle_ValidBirthday_ReturnsValidTitle() throws Exception {

        Birthday bd = createBirthday("John", Date.APRIL, 23);
        assertThat(bd.getName(), is("John"));
    }

    @Test public void getDate_ValidBirthday_ReturnsValidDayAndMonth() throws Exception {
        int day = 23;
        @Date.Month int month = Date.FEBRUARY;
        Birthday bd = createBirthday("John", month, day);
        assertThat(bd.getDate().getDayOfMonth(), is(day));
        assertThat(bd.getDate().getMonthOfYear(), is(month));
    }

    @Test public void getDate_SameAsNow() throws Exception {
        LocalDate now = new LocalDate(2013, 7, 23);
        when(clock.now()).thenReturn(now);
        @SuppressWarnings("ResourceType")
        Birthday bd = createBirthday("Fred", now.getMonthOfYear(), now.getDayOfMonth());
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now));
    }

    @Test public void getDate_AfterNow() throws Exception {
        LocalDate now = new LocalDate(2014, 6, 5);
        when(clock.now()).thenReturn(now);
        @SuppressWarnings("ResourceType")
        Birthday bd = createBirthday("Joe", now.getMonthOfYear(), now.getDayOfMonth() + 1);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.plusDays(1)));
    }

    @Test public void getDate_BeforeNow() throws Exception {
        LocalDate now = new LocalDate(2014, 6, 5);
        when(clock.now()).thenReturn(now);
        @SuppressWarnings("ResourceType")
        Birthday bd = createBirthday("Joe", now.getMonthOfYear(), now.getDayOfMonth() - 1);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.minusDays(1).plusYears(1)));
    }

    @Test public void getDate_FirstDayOfYearAskedOnLastDayOfYear() throws Exception {
        LocalDate now = new LocalDate(2014, 12, 31);
        when(clock.now()).thenReturn(now);
        Birthday bd = createBirthday("Joe", Date.JANUARY, 1);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.plusDays(1)));
    }

    @Test public void getDate_LastDayOfYearAskedOnFirstDayOfYear() throws Exception {
        LocalDate now = new LocalDate(2014, 1, 1);
        when(clock.now()).thenReturn(now);
        Birthday bd = createBirthday("Joe", Date.DECEMBER, 31);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(new LocalDate(2014, 12, 31)));
    }

    @Test public void compareTo_NowIsAfter() throws Exception {
        LocalDate now = new LocalDate(2014, 7, 15);
        when(clock.now()).thenReturn(now);
        Birthday joe = createBirthday("joe", Date.MARCH, 4);
        Birthday fred = createBirthday("fred", Date.NOVEMBER, 5);

        assertThat(joe.compareTo(fred), is(1));
    }

    @Test public void compareTo_NowIsBefore() throws Exception {
        LocalDate now = new LocalDate(2014, 1, 15);
        when(clock.now()).thenReturn(now);
        Birthday joe = createBirthday("joe", Date.MARCH, 4);
        Birthday fred = createBirthday("fred", Date.NOVEMBER, 5);

        assertThat(joe.compareTo(fred), is(-1));
    }

    @Test public void archiveEventWithYearToBundle() {
        when(uniqueIdGenerator.getUniqueId()).thenReturn("string-id");
        when(uniqueIdGenerator.hashCode("string-id")).thenReturn(666);
        Event event = createBirthday("Fred", 2015, Date.AUGUST, 23);
        Bundle bundle = mock(Bundle.class);
        event.archiveTo(bundle);

        verify(bundle).putString(IEvent.KEY_TYPE, Birthday.class.getCanonicalName());
        verify(bundle).putInt(IEvent.KEY_ID, 666);
        verify(bundle).putString(IEvent.KEY_STRING_ID, "string-id");
        verify(bundle).putString(IEvent.KEY_NAME, "Fred");
        verify(bundle, never()).putString(eq(Birthday.KEY_LAST_NAME), anyString());
        verify(bundle).putInt(IEvent.KEY_YEAR, 2015);
        verify(bundle).putInt(IEvent.KEY_MONTH, Date.AUGUST);
        verify(bundle).putInt(IEvent.KEY_DAY, 23);
    }
    @Test public void archiveBirthdayWithLastNameToBundle() {
        when(uniqueIdGenerator.getUniqueId()).thenReturn("string-id");
        when(uniqueIdGenerator.hashCode("string-id")).thenReturn(666);
        Event event = createBirthday("Fred", "Doe", Date.AUGUST, 23);
        Bundle bundle = mock(Bundle.class);
        event.archiveTo(bundle);

        verify(bundle).putString(IEvent.KEY_TYPE, Birthday.class.getCanonicalName());
        verify(bundle).putInt(IEvent.KEY_ID, 666);
        verify(bundle).putString(IEvent.KEY_STRING_ID, "string-id");
        verify(bundle).putString(IEvent.KEY_NAME, "Fred");
        verify(bundle).putString(Birthday.KEY_LAST_NAME, "Doe");
        verify(bundle, never()).putInt(eq(IEvent.KEY_YEAR), anyInt());
        verify(bundle).putInt(IEvent.KEY_MONTH, Date.AUGUST);
        verify(bundle).putInt(IEvent.KEY_DAY, 23);
    }


    @Test public void testSerializeBirthday() throws Exception {
        Birthday bd = createBirthday("Mine", Date.FEBRUARY, 8);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);
        assertThatJson(json).node(Birthday.KEY_NAME).isEqualTo("Mine");
        assertThatJson(json).node(Birthday.KEY_MONTH).isEqualTo(Date.FEBRUARY);
        assertThatJson(json).node(Birthday.KEY_DAY).isEqualTo(8);
        assertThatJson(json).node(Birthday.KEY_YEAR).isAbsent();
        assertThatJson(json).node(Birthday.KEY_TYPE).isEqualTo(Birthday.class.getCanonicalName());
        assertThatJson(json).node(Birthday.KEY_NBR_DAYS_FOR_NOTIFICATION).isEqualTo(2);
    }

    @Test public void testSerializeBirthday_WithValidYearOfBirth() throws Exception {
        Birthday bd = createBirthday("Mine", 1966, Date.FEBRUARY, 8);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);
        assertThatJson(json).node(Birthday.KEY_NAME).isEqualTo("Mine");
        assertThatJson(json).node(Birthday.KEY_MONTH).isEqualTo(Date.FEBRUARY);
        assertThatJson(json).node(Birthday.KEY_DAY).isEqualTo(8);
        assertThatJson(json).node(Birthday.KEY_YEAR).isEqualTo(1966);
        assertThatJson(json).node(Birthday.KEY_TYPE).isEqualTo(Birthday.class.getCanonicalName());
    }

    @Test public void testSerializeBirthdayWithLastName() throws Exception {
        Birthday bd = createBirthday("First", "Last", Date.FEBRUARY, 8);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);
        assertThatJson(json).node(Birthday.KEY_NAME).isEqualTo("First");
        assertThatJson(json).node(Birthday.KEY_LAST_NAME).isEqualTo("Last");
        assertThatJson(json).node(Birthday.KEY_MONTH).isEqualTo(Date.FEBRUARY);
        assertThatJson(json).node(Birthday.KEY_DAY).isEqualTo(8);
        assertThatJson(json).node(Birthday.KEY_YEAR).isAbsent();
        assertThatJson(json).node(Birthday.KEY_TYPE).isEqualTo(Birthday.class.getCanonicalName());
        assertThatJson(json).node(Birthday.KEY_NBR_DAYS_FOR_NOTIFICATION).isEqualTo(2);
    }

    @Test public void testSerializeBirthday_WithValidYearOfBirthAndLastName() throws Exception {
        Birthday bd = createBirthday("First", "Last", 1966, Date.FEBRUARY, 8);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);
        assertThatJson(json).node(Birthday.KEY_NAME).isEqualTo("First");
        assertThatJson(json).node(Birthday.KEY_LAST_NAME).isEqualTo("Last");
        assertThatJson(json).node(Birthday.KEY_MONTH).isEqualTo(Date.FEBRUARY);
        assertThatJson(json).node(Birthday.KEY_DAY).isEqualTo(8);
        assertThatJson(json).node(Birthday.KEY_YEAR).isEqualTo(1966);
        assertThatJson(json).node(Birthday.KEY_TYPE).isEqualTo(Birthday.class.getCanonicalName());
    }

    @Test public void testDeserializeBirthDay() throws Exception {

        Birthday bd = createBirthday("Mine", Date.FEBRUARY, 8);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);

        builder.registerTypeAdapter(Birthday.class, new BirthdayInstanceCreator(clock, uniqueIdGenerator));
        gson = builder.create();
        Birthday readBirthday = gson.fromJson(json, Birthday.class);
        assertThat(readBirthday.getName(), is("Mine"));
        assertThat(readBirthday.getType(), is(Birthday.class.getCanonicalName()));
    }

    @Test public void testToStringBirthdayNameMonthDay () {
        Birthday bd = createBirthday("Joe", Date.APRIL, 23);

        String string = bd.toString();
        assertThat(string, is("Joe - 23-04"));
    }

    @Test public void testToStringBirthdayNameLastNameMonthDay () {
        Birthday bd = createBirthday("John", "Doe", Date.JANUARY, 15);
        String string = bd.toString();
        assertThat(string, is("John Doe - 15-01"));
    }
    @Test public void testToStringBirthdayNameYearMonthDay() {
        Birthday bd = createBirthday("Joe", 2015, Date.AUGUST, 23);

        String string = bd.toString();
        assertThat(string, is("Joe - 23-08-2015"));
    }
    @Test public void testToStringBirthdayNameLastNameYearMonthDay() {
        Birthday bd = createBirthday("Joe", "Doe", 2015, Date.AUGUST, 23);

        String string = bd.toString();
        assertThat(string, is("Joe Doe - 23-08-2015"));
    }

    private Birthday createBirthday(String name, @Date.Month int month, int day) {
        return new Birthday(name, month, day, clock, uniqueIdGenerator);
    }
    private Birthday createBirthday(String name,int year, @Date.Month int month, int day) {
        return new Birthday(name, year, month, day, clock, uniqueIdGenerator);
    }
    private Birthday createBirthday(String name, String lastName,int year, @Date.Month int month, int day) {
        return new Birthday(name, lastName, year, month, day, clock, uniqueIdGenerator);
    }
    private Birthday createBirthday(String name, String lastName, @Date.Month int month, int day) {
        return new Birthday(name, lastName, month, day, clock, uniqueIdGenerator);
    }
}
