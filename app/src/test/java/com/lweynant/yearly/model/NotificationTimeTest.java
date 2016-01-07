package com.lweynant.yearly.model;

import android.support.annotation.NonNull;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTimeTest {
    @Mock
    IClock clock;
    @Mock
    IUniqueIdGenerator uniqueIdGenerator;


    @Test
    public void test_getAlarmDateAndHour_EventIsNow() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);

        NotificationTime sut = new NotificationTime(now, getEvent(now));
        assertThat(sut.getAlarmDate(), is(now));
        assertThat(sut.getHour(), is(NotificationTime.MORNING));
    }

    @Test
    public void test_getAlarmDateAndHour_EventIsTomorrow() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);

        LocalDate tomorrow = now.plusDays(1);
        NotificationTime sut = new NotificationTime(now, getEvent(tomorrow));
        assertThat(sut.getAlarmDate(), is(now));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test
    public void test_getAlarmDateAndHour_EventIsInFuture() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);

        LocalDate future = now.plusDays(100);
        Event event = getEvent(future);
        NotificationTime sut = new NotificationTime(now, event);
        assertThat(sut.getAlarmDate(), is(future.minusDays(event.getNbrOfDaysForNotification())));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test
    public void test_getAlarmDateAndHour_EventIsYesterday() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);

        LocalDate yesterday = now.minusDays(1);
        Event event = getEvent(yesterday);
        NotificationTime sut = new NotificationTime(now, event);
        assertThat(sut.getAlarmDate(), is(yesterday.plusYears(1).minusDays(event.getNbrOfDaysForNotification())));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test
    public void test_isBefore_OtherIsSame() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);
        LocalDate eventDate = now.plusDays(6);

        NotificationTime sut = new NotificationTime(now, getEvent(eventDate));
        NotificationTime other = new NotificationTime(now, getEvent(eventDate));
        assertThat(sut.isBefore(other), is(false));
    }

    @Test
    public void test_isBefore_OtherIsAfter() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);
        LocalDate eventDate = now.plusDays(6);

        NotificationTime sut = new NotificationTime(now, getEvent(eventDate));
        NotificationTime other = new NotificationTime(now, getEvent(eventDate.plusDays(4)));
        assertThat(sut.isBefore(other), is(true));
    }

    @Test
    public void test_isBefore_OtherIsBefore() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);
        LocalDate eventDate = now.plusDays(6);

        NotificationTime sut = new NotificationTime(now, getEvent(eventDate));
        NotificationTime other = new NotificationTime(now, getEvent(eventDate.minusDays(4)));
        assertThat(sut.isBefore(other), is(false));
    }

    @Test
    public void test_isBefore_SutIsNowOtherIsTomorrow() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);

        NotificationTime sut = new NotificationTime(now, getEvent(now));
        NotificationTime other = new NotificationTime(now, getEvent(now.plusDays(1)));
        assertThat(sut.isBefore(other), is(true));
    }

    @Test
    public void testDaysBeforeNotification_EventAfterFrom() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);
        Event event = new Event("name", Date.MARCH, 10, clock, uniqueIdGenerator);
        LocalDate from = now;
        NotificationTime sut = new NotificationTime(from, event);
        assertThat(sut.getAlarmDate(), is(from.plusDays(8)));
        assertThat(sut.getHour(), is(19));
    }

    @Test
    public void testDaysBeforeNotification_EventSameAsFrom() throws Exception {
        LocalDate now = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(now);
        Event event = getEvent(now);
        LocalDate from = now;
        NotificationTime sut = new NotificationTime(from, event);
        assertThat(sut.getAlarmDate(), is(from));
        assertThat(sut.getHour(), is(6));
    }

    @NonNull
    private Event getEvent(LocalDate date) {
        return new Event("event name", date.getMonthOfYear(), date.getDayOfMonth(), clock, uniqueIdGenerator);
    }

    @Test
    public void testDaysBeforeNotification_EventBeforeFrom() throws Exception {
        LocalDate eventDate = new LocalDate(2015, Date.MARCH, 1);
        when(clock.now()).thenReturn(eventDate);
        Event event = getEvent(eventDate);
        LocalDate from = eventDate.plusDays(1);
        NotificationTime sut = new NotificationTime(from, event);
        assertThat(sut.getAlarmDate(), is(from.plusDays(364)));
        assertThat(sut.getHour(), is(19));
    }

}
