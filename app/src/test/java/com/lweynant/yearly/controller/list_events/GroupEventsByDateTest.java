package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.IStringResources;
import com.lweynant.yearly.R;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.exceptions.ExceptionIncludingMockitoWarnings;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupEventsByDateTest {
    @Mock IEvent event;
    @Mock IStringResources stringResources;
    @Mock IClock clock;
    LocalDate today;
    LocalDate tomorrow;
    LocalDate dayAfterTomorrow;
    private java.lang.String[] near_future = { "today", "tomorrow", "day after tomorrow"};
    private String[] months = { "", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    @Before public void setUp(){
        today = new LocalDate(2000, Date.APRIL, 20);
        tomorrow = today.plusDays(1);
        dayAfterTomorrow = tomorrow.plusDays(1);
        when(clock.now()).thenReturn(today);
        when(stringResources.getStringArray(R.array.near_future)).thenReturn(near_future);
        when(stringResources.getStringArray(R.array.months)).thenReturn(months);
    }


    @Test public void group_eventThatHappensToday() throws Exception {
        GroupEventsByDate sut = new GroupEventsByDate(clock, stringResources);
        when(event.getDate()).thenReturn(today);
        String group = sut.group(event);
        assertThat(group, is("today"));
    }

    @Test public void group_eventThatHappensTommorow() throws Exception {
        GroupEventsByDate sut = new GroupEventsByDate(clock, stringResources);
        when(event.getDate()).thenReturn(tomorrow);
        String group = sut.group(event);
        assertThat(group, is("tomorrow"));
    }

    @Test public void group_eventThatHappensDayAfterTomorrow() throws Exception {
        GroupEventsByDate sut = new GroupEventsByDate(clock, stringResources);
        when(event.getDate()).thenReturn(dayAfterTomorrow);
        String group = sut.group(event);
        assertThat(group, is("day after tomorrow"));
    }

    @Test public void group_eventThatHappensInJune() throws Exception {
        GroupEventsByDate sut = new GroupEventsByDate(clock, stringResources);
        when(event.getDate()).thenReturn(new LocalDate(2000, Date.JUNE, 3));
        String group = sut.group(event);
        assertThat(group, is("Jun"));
    }

    @Test public void group_eventThatHappensTomorrow_NoNearFutureForTomorrow() throws Exception {
        when(event.getDate()).thenReturn(tomorrow);
        when(stringResources.getStringArray(R.array.near_future)).thenReturn(new String[]{"Today"});
        GroupEventsByDate sut = new GroupEventsByDate(clock, stringResources);
        String group = sut.group(event);
        assertThat(group, is(months[tomorrow.getMonthOfYear()]));
    }
}