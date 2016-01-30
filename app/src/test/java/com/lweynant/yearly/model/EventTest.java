package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.assertNull;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {


    private final String name = "event name";
    @Mock IClock clock;
    @Mock IUniqueIdGenerator uniqueIdGenerator;


    @Test
    public void testID() throws Exception {
        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(45);
        Event event = new Event(name, Date.AUGUST, 20, clock, uniqueIdGenerator);

        assertThat(event.getID(), is(45));
    }

    @Test public void testYearOfOriginNotAvailable () {
        Event event = createEvent(name, Date.APRIL, 23);

        assertThat(event.hasYearOfOrigin(), is(false));
        assertNull(event.getYearOfOrigin());
    }
    @Test public void testYearOfOriginAvailable () {
        Event event = createEvent(name, 2000, Date.APRIL, 23);

        assertThat(event.hasYearOfOrigin(), is(true));
        assertThat(event.getYearOfOrigin(), is(2000));
    }


    @Test public void testSerializeWithoutYear() throws Exception {
        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String name = "event name";
        Event event = createEvent(name, Date.AUGUST, 30);
        String json = gson.toJson(event);
        assertThatJson(json).node(Event.KEY_NAME).isEqualTo(name);
        assertThatJson(json).node(Event.KEY_TYPE).isEqualTo(Event.class.getCanonicalName());
        assertThatJson(json).node(Event.KEY_NBR_DAYS_FOR_NOTIFICATION).isEqualTo(1);
        assertThatJson(json).node(Event.KEY_DAY).isEqualTo(30);
        assertThatJson(json).node(Event.KEY_MONTH).isEqualTo(Date.AUGUST);
        assertThatJson(json).node(Event.KEY_UID).isEqualTo("random-id");
        assertThatJson(json).node(Event.KEY_ID).isEqualTo(55);
        assertThatJson(json).node(Event.KEY_YEAR).isAbsent();
    }
    @Test public void testSerializeWithYear() throws Exception {
        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String name = "event name";
        Event event = createEvent(name, 2016, Date.AUGUST, 30);
        String json = gson.toJson(event);
        assertThatJson(json).node(Event.KEY_NAME).isEqualTo(name);
        assertThatJson(json).node(Event.KEY_TYPE).isEqualTo(Event.class.getCanonicalName());
        assertThatJson(json).node(Event.KEY_NBR_DAYS_FOR_NOTIFICATION).isEqualTo(1);
        assertThatJson(json).node(Event.KEY_DAY).isEqualTo(30);
        assertThatJson(json).node(Event.KEY_MONTH).isEqualTo(Date.AUGUST);
        assertThatJson(json).node(Event.KEY_UID).isEqualTo("random-id");
        assertThatJson(json).node(Event.KEY_ID).isEqualTo(55);
        assertThatJson(json).node(Event.KEY_YEAR).isEqualTo(2016);
    }

    @Test public void testCreateFromGson() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);

        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String name = "event name";
        Event expectedEvent = createEvent(name, Date.AUGUST, 30);
        String json = gson.toJson(expectedEvent);


        gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Event.class, new EventInstanceCreator(clock, uniqueIdGenerator))
                .create();
        Event event = gson.fromJson(json, Event.class);
        assertThat(event.getType(), is(Event.class.getCanonicalName()));
        assertThat(event.getID(), is(55));
        assertThat(event.getName(), is(name));
        assertNull(event.getYearOfOrigin());
        LocalDate date = event.getDate();
        assertThat(date.getYear(), is(2015));
    }
    @Test public void testCreateFromGson_WithYear() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);

        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String name = "event name";
        Event expectedEvent = createEvent(name, 2016, Date.AUGUST, 30);
        String json = gson.toJson(expectedEvent);


        gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Event.class, new EventInstanceCreator(clock, uniqueIdGenerator))
                .create();
        Event event = gson.fromJson(json, Event.class);
        assertThat(event.getType(), is(Event.class.getCanonicalName()));
        assertThat(event.getID(), is(55));
        assertThat(event.getName(), is(name));
        assertThat(event.getYearOfOrigin(), is(2016));
        LocalDate date = event.getDate();
        assertThat(date.getYear(), is(2015));
    }
    private Event createEvent(String name, int year, int month, int day) {
        return new Event(name, year, month, day, clock, uniqueIdGenerator);
    }

    private Event createEvent(String name, int month, int day) {
        return new Event(name, month, day, clock, uniqueIdGenerator);
    }

}
