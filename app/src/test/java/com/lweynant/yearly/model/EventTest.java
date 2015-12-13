package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonObject;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Type;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventTest {


    @Mock
    IClock clock;
    @Mock
    IUUID iuuid;

    @Test
    public void testDaysBeforeNotification_EventAfterFrom() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);
        Event sut = new Event(Date.MARCH, 10, clock, iuuid);
        LocalDate from = now;
        TimeBeforeNotification days = Event.timeBeforeNotification(from, sut);
        assertThat(days.getDays(), is(8));
        assertThat(days.getHour(), is(19));
    }
    @Test
    public void testDaysBeforeNotification_EventSameAsFrom() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);
        Event sut = new Event(now.getMonthOfYear(), now.getDayOfMonth(), clock, iuuid);
        LocalDate from = now;
        TimeBeforeNotification days = Event.timeBeforeNotification(from, sut);
        assertThat(days.getDays(), is(0));
        assertThat(days.getHour(), is(6));
    }
    @Test
    public void testDaysBeforeNotification_EventBeforeFrom() throws Exception {
        LocalDate eventDate = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(eventDate);
        Event sut = new Event(eventDate.getMonthOfYear(), eventDate.getDayOfMonth(), clock, iuuid);
        LocalDate from = eventDate.plusDays(1);
        TimeBeforeNotification days = Event.timeBeforeNotification(from, sut);
        assertThat(days.getDays(), is(364));
        assertThat(days.getHour(), is(19));
    }

    @Test
    public void testShouldEventBeNotified() throws Exception{
        LocalDate eventDate = new LocalDate(2015, Date.JULY, 8);
        LocalDate now = eventDate.minusDays(1);
        when(clock.now()).thenReturn(now);
        Event event = new Event(eventDate.getMonthOfYear(), eventDate.getDayOfMonth(), clock, iuuid);
        boolean notify = Event.shouldBeNotified(now, event);
    }

    @Test
    public void testID() throws Exception{
        when(iuuid.getRandomUID()).thenReturn("random-id");
        when(iuuid.hashCode("random-id")).thenReturn(45);
        Event event= new Event(Date.AUGUST, 20, clock, iuuid);

        assertThat(event.getID(), is(45));
    }

    @Test
    public void testSerialize() throws Exception{
        when(iuuid.getRandomUID()).thenReturn("random-id");
        when(iuuid.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Event event = new Event(Date.AUGUST, 30, clock, iuuid);
        String json = gson.toJson(event);
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

        when(iuuid.getRandomUID()).thenReturn("random-id");
        when(iuuid.hashCode("random-id")).thenReturn(55);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Event expectedEvent = new Event(Date.AUGUST, 30, clock, iuuid);
        String json = gson.toJson(expectedEvent);


        gson = new GsonBuilder()
                //.excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Event.class, new EventInstanceCreator(clock, iuuid))
                .create();
        Event event = gson.fromJson(json, Event.class);
        assertThat(event.getType(), is(Event.class.getCanonicalName()));
        assertThat(event.getID(), is(55));

        LocalDate date = event.getDate();
        assertThat(date.getYear(), is(2015));
    }

}
