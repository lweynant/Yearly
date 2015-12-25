package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {


    @Mock
    IClock clock;
    @Mock
    IUniqueIdGenerator uniqueIdGenerator;
    private final String name = "event name";


    @Test
    public void testShouldEventBeNotified() throws Exception{
        LocalDate eventDate = new LocalDate(2015, Date.JULY, 8);
        LocalDate now = eventDate.minusDays(1);
        when(clock.now()).thenReturn(now);
        Event event = new Event(name, eventDate.getMonthOfYear(), eventDate.getDayOfMonth(), clock, uniqueIdGenerator);
        boolean notify = Event.shouldBeNotified(now, event);
    }

    @Test
    public void testID() throws Exception{
        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(45);
        Event event= new Event(name, Date.AUGUST, 20, clock, uniqueIdGenerator);

        assertThat(event.getID(), is(45));
    }

    @Test
    public void testSerialize() throws Exception{
        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String name = "event name";
        Event event = new Event(name, Date.AUGUST, 30, clock, uniqueIdGenerator);
        String json = gson.toJson(event);
        assertThatJson(json).node(Event.KEY_NAME).isEqualTo(name);
        assertThatJson(json).node(Event.KEY_TYPE).isEqualTo(Event.class.getCanonicalName());
        assertThatJson(json).node(Event.KEY_NBR_DAYS_FOR_NOTIFICATION).isEqualTo(1);
        assertThatJson(json).node(Event.KEY_DAY).isEqualTo(30);
        assertThatJson(json).node(Event.KEY_MONTH).isEqualTo(Date.AUGUST);
        assertThatJson(json).node(Event.KEY_UID).isEqualTo("random-id");
        assertThatJson(json).node(Event.KEY_ID).isEqualTo(55);
    }
    @Test
    public void testCreateFromGson() throws Exception{
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);

        when(uniqueIdGenerator.getUniqueId()).thenReturn("random-id");
        when(uniqueIdGenerator.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String name = "event name";
        Event expectedEvent = new Event(name, Date.AUGUST, 30, clock, uniqueIdGenerator);
        String json = gson.toJson(expectedEvent);


        gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Event.class, new EventInstanceCreator(clock, uniqueIdGenerator))
                .create();
        Event event = gson.fromJson(json, Event.class);
        assertThat(event.getType(), is(Event.class.getCanonicalName()));
        assertThat(event.getID(), is(55));
        assertThat(event.getName(), is(name));
        LocalDate date = event.getDate();
        assertThat(date.getYear(), is(2015));
    }

}
