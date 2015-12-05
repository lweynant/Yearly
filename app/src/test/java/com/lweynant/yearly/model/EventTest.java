package com.lweynant.yearly.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lweynant.yearly.util.IClock;

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

    @Test
    public void testDaysBeforeNotification_EventAfterFrom() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);
        Event sut = new Event(Date.MARCH, 10, clock);
        LocalDate from = now;
        TimeBeforeNotification days = Event.timeBeforeNotification(from, sut);
        assertThat(days.getDays(), is(8));
        assertThat(days.getHour(), is(19));
    }
    @Test
    public void testDaysBeforeNotification_EventSameAsFrom() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);
        Event sut = new Event(now.getMonthOfYear(), now.getDayOfMonth(), clock);
        LocalDate from = now;
        TimeBeforeNotification days = Event.timeBeforeNotification(from, sut);
        assertThat(days.getDays(), is(0));
        assertThat(days.getHour(), is(6));
    }
    @Test
    public void testDaysBeforeNotification_EventBeforeFrom() throws Exception {
        LocalDate eventDate = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(eventDate);
        Event sut = new Event(eventDate.getMonthOfYear(), eventDate.getDayOfMonth(), clock);
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
        Event event = new Event(eventDate.getMonthOfYear(), eventDate.getDayOfMonth(), clock);
        boolean notify = Event.shouldBeNotified(now, event);
    }

    @Test
    public void testSerialize() throws Exception{
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Event event = new Event(Date.AUGUST, 30, clock);
        String json = gson.toJson(event);
        assertThatJson(json).node("type").isEqualTo(Event.class.getCanonicalName());
        assertThatJson(json).node("nbr_days_for_notification").isEqualTo(1);
        assertThatJson(json).node("day").isEqualTo(30);
        assertThatJson(json).node("month").isEqualTo(Date.AUGUST);
    }

}
