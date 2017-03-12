package com.lweynant.yearly.controller.list_events;

import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.lweynant.yearly.model.Date.MARCH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WeekCheckerTest {
    @Mock IClock clock;
    LocalDate wednesday = new LocalDate(2017, MARCH, 8);
    WeekChecker sut;

    @Before public void setUp(){
        sut = new WeekChecker(clock);
    }

    @Test public void testTodayIsThisWeek(){
        setTodayOnClock(wednesday);
        assertThat(sut.isThisWeek(wednesday), is(true));
    }

    @Test public void testYesterdayIsNotThisWeek() {
        setTodayOnClock(wednesday.plusDays(1));
        assertThat(sut.isThisWeek(wednesday), is(false));
    }

    @Test public void testTodayPlusWeekIsNotThisWeek() {
        setTodayOnClock(wednesday.minusWeeks(1));
        assertThat(sut.isThisWeek(wednesday), is(false));
    }
    @Test public void testMondayIsNextWeek() {
        LocalDate monday = wednesday.plusDays(5);
        setTodayOnClock(wednesday);
        assertThat(sut.isNextWeek(monday), is(true));
    }

    @Test public void testSundayIsThisWeek() {
        LocalDate sunday = wednesday.plusDays(4);
        setTodayOnClock(wednesday);
        assertThat(sut.isThisWeek(sunday), is(true));
    }
    @Test public void testLastSundayIsNotThisWeek() {
        LocalDate sunday = wednesday.minusDays(3);
        setTodayOnClock(wednesday);
        assertThat(sut.isThisWeek(sunday), is(false));
    }

    @Test public void testTodayPlusWeekIsNextWeek(){
        setTodayOnClock(wednesday.minusWeeks(1));
        assertThat(sut.isNextWeek(wednesday), is(true));
    }

    private void setTodayOnClock(LocalDate date) {
        when(clock.now()).thenReturn(date);
    }
}
