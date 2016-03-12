package com.lweynant.yearly.model;

import android.support.annotation.NonNull;

import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NotificationTimeTest {

    @Mock IClock clock;
    private LocalDate now;

    @Before public void setUp() {
        now = new LocalDate(2015, Date.FEBRUARY, 23);
        when(clock.now()).thenReturn(now);
    }

    @Test public void startOfDayNotificationForTodaysEvent() throws Exception {

        NotificationTime sut = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(now));
        assertThat(sut.getAlarmDate(), is(now));
        assertThat(sut.getHour(), is(NotificationTime.MORNING));
    }
    @Test public void morningNotificationForTodaysEvent() throws Exception {

        IEvent event = createEvent(now);
        NotificationTime sut = new NotificationTime(now, NotificationTime.MORNING, event);
        assertThat(sut.getAlarmDate(), is(now.plusYears(1).minusDays(event.getNbrOfDaysForNotification())));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }
    @Test public void eveningNotificationForTodaysEvent() throws Exception {

        IEvent event = createEvent(now);
        NotificationTime sut = new NotificationTime(now, NotificationTime.EVENING, event);
        assertThat(sut.getAlarmDate(), is(now.plusYears(1).minusDays(event.getNbrOfDaysForNotification())));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test public void morningNotificationForTomorrowsEvent() throws Exception {

        LocalDate tomorrow = now.plusDays(1);
        NotificationTime sut = new NotificationTime(now, NotificationTime.MORNING, createEvent(tomorrow));
        assertThat(sut.getAlarmDate(), is(now));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }
    @Test public void eveningNotificationForTomorrowsEvent() throws Exception {

        LocalDate tomorrow = now.plusDays(1);
        NotificationTime sut = new NotificationTime(now, NotificationTime.EVENING, createEvent(tomorrow));
        assertThat(sut.getAlarmDate(), is(tomorrow));
        assertThat(sut.getHour(), is(NotificationTime.MORNING));
    }

    @Test public void eveningNotificationForFutureEvent() throws Exception {

        LocalDate future = now.plusDays(100);
        IEvent event = createEvent(future);
        NotificationTime sut = new NotificationTime(now, NotificationTime.EVENING, event);
        assertThat(sut.getAlarmDate(), is(future.minusDays(event.getNbrOfDaysForNotification())));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test public void morningNotificationForYesterdaysEvent() throws Exception {

        LocalDate yesterday = now.minusDays(1);
        IEvent event = createEvent(yesterday);
        NotificationTime sut = new NotificationTime(now, NotificationTime.EVENING, event);
        assertThat(sut.getAlarmDate(), is(yesterday.plusYears(1).minusDays(event.getNbrOfDaysForNotification())));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test   public void test_isBefore_OtherIsSame() throws Exception {
        LocalDate eventDate = now.plusDays(6);

        NotificationTime sut = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(eventDate));
        NotificationTime other = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(eventDate));
        assertThat(sut.isBefore(other), is(false));
    }

    @Test
    public void test_isBefore_OtherIsAfter() throws Exception {
        LocalDate eventDate = now.plusDays(6);

        NotificationTime sut = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(eventDate));
        NotificationTime other = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(eventDate.plusDays(4)));
        assertThat(sut.isBefore(other), is(true));
    }

    @Test
    public void test_isBefore_OtherIsBefore() throws Exception {
        LocalDate now = new LocalDate(2015, Date.FEBRUARY, 23);
        LocalDate eventDate = now.plusDays(6);

        NotificationTime sut = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(eventDate));
        NotificationTime other = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(eventDate.minusDays(4)));
        assertThat(sut.isBefore(other), is(false));
    }

    @Test
    public void test_isBefore_SutIsNowOtherIsTomorrow() throws Exception {

        NotificationTime sut = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(now));
        NotificationTime other = new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(now.plusDays(1)));
        assertThat(sut.isBefore(other), is(true));
    }

    @Test
    public void testDaysBeforeNotification_EventAfterFrom() throws Exception {
        IEvent event = createEvent(now.plusDays(9));
        LocalDate from = now;
        NotificationTime sut = new NotificationTime(from, NotificationTime.START_OF_DAY, event);
        assertThat(sut.getAlarmDate(), is(from.plusDays(8)));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test
    public void testDaysBeforeNotification_EventSameAsFrom() throws Exception {
        IEvent event = createEvent(now);
        LocalDate from = now;
        NotificationTime sut = new NotificationTime(from, NotificationTime.START_OF_DAY, event);
        assertThat(sut.getAlarmDate(), is(from));
        assertThat(sut.getHour(), is(NotificationTime.MORNING));
    }

    @NonNull
    private IEvent createEvent(LocalDate date) {
        IEvent event = mock(IEvent.class);
        when(event.getDate()).thenReturn(date);
        when(event.getNbrOfDaysForNotification()).thenReturn(1);
        return event;
    }

    @Test
    public void testDaysBeforeNotification_EventBeforeFrom() throws Exception {
        LocalDate eventDate = new LocalDate(2015, Date.MARCH, 1);
        IEvent event = createEvent(eventDate);
        LocalDate from = eventDate.plusDays(1);
        NotificationTime sut = new NotificationTime(from,NotificationTime.START_OF_DAY, event);
        assertThat(sut.getAlarmDate(), is(from.plusDays(364)));
        assertThat(sut.getHour(), is(NotificationTime.EVENING));
    }

    @Test public void minRhsIsSmaller() {
        LocalDate first = now.plusDays(5);
        LocalDate later = first.plusDays(6);
        NotificationTime rhs = createNotificationTime(now, first);
        NotificationTime lhs = createNotificationTime(now, later);
        NotificationTime min = NotificationTime.min(rhs, lhs);
        assertThat(min, is(rhs));
    }
    @Test public void minRhsIsLater() {
        LocalDate first = now.plusDays(5);
        LocalDate later = first.plusDays(6);
        NotificationTime rhs = createNotificationTime(now, later);
        NotificationTime lhs = createNotificationTime(now, first);
        NotificationTime min = NotificationTime.min(rhs, lhs);
        assertThat(min, is(lhs));
    }
    @Test public void minRhsIsSame() {
        LocalDate date = now.plusDays(5);
        NotificationTime rhs = createNotificationTime(now, date);
        NotificationTime lhs = createNotificationTime(now, date);
        NotificationTime min = NotificationTime.min(rhs, lhs);
        assertThat(min, is(lhs));
    }

    @Test public void todaysEventShouldBeNotifiedAtStartOfDay() {
        IEvent event = createEvent(now);
        when(clock.hour()).thenReturn(NotificationTime.START_OF_DAY);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(true));
    }
    @Test public void todaysEventShouldBeNotifiedAtMorning() {
        IEvent event = createEvent(now);
        when(clock.hour()).thenReturn(NotificationTime.MORNING);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(true));
    }
    @Test public void todaysEventShouldNotBeNotifiedAtEvening() {
        IEvent event = createEvent(now);
        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(false));
    }
    @Test public void tomorrowsEventShouldNotBeNotifiedAtStartOfDay() {
        IEvent event = createEvent(now.plusDays(1));
        when(clock.hour()).thenReturn(NotificationTime.START_OF_DAY);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(false));
    }
    @Test public void tomorrowsEventShouldNotBeNotifiedAtMorning() {
        IEvent event = createEvent(now.plusDays(1));
        when(clock.hour()).thenReturn(NotificationTime.MORNING);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(false));
    }
    @Test public void tomorrowsEventShouldBeNotifiedAtEvening() {
        IEvent event = createEvent(now.plusDays(1));
        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(true));
    }
    @Test public void outsideNotificationPeriodEventShouldNotifiedAtEvening() {
        IEvent event = createEvent(now.plusDays(3));
        when(event.getNbrOfDaysForNotification()).thenReturn(2);
        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(false));
    }
    @Test public void insideNotificationPeriodEventShouldNotifiedAtEvening() {
        IEvent event = createEvent(now.plusDays(3));
        when(event.getNbrOfDaysForNotification()).thenReturn(3);
        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        assertThat(NotificationTime.shouldBeNotified(clock, event), is(true));
    }

    private NotificationTime createNotificationTime(LocalDate now, LocalDate date) {
        return new NotificationTime(now, NotificationTime.START_OF_DAY, createEvent(date));
    }

}
