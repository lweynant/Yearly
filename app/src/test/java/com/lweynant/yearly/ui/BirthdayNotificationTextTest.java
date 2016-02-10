package com.lweynant.yearly.ui;

import com.lweynant.yearly.R;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BirthdayNotificationTextTest {

    @Mock private IEvent event;
    @Mock private IEventStringResource rstring;
    @Mock private IClock clock;
    private BirthdayNotificationText sut;
    private LocalDate today;
    private LocalDate tomorrow;
    private LocalDate dayAfterTomorrow;

    @Before public void setUp() {
        today = new LocalDate(2016, Date.FEBRUARY, 5);
        tomorrow = today.plusDays(1);
        dayAfterTomorrow = tomorrow.plusDays(1);
        when(clock.now()).thenReturn(today);
        sut = new BirthdayNotificationText(event, rstring, clock);

    }
    @Test public void getTitle_FirstLetterIsCapitalized() {
        when(rstring.getFormattedTitle(event)).thenReturn("event title");
        String title = sut.getTitle();
        assertThat(title, is("Event title"));
    }
    @Test public void getTitle_FirstLetterRemainsCapitalized() {
        when(rstring.getFormattedTitle(event)).thenReturn("Event title");
        String title = sut.getTitle();
        assertThat(title, is("Event title"));
    }

    @Test public void getText_ForEventThatTriggersToday() {
        when(event.getDate()).thenReturn(today);
        when(rstring.getStringFromId(R.string.today)).thenReturn("today");
        String text = sut.getText();

        assertThat(text, is("Today " + getDateAsText(today)));
    }
    @Test public void getText_ForEventThatTriggersTomorrow() {
        when(event.getDate()).thenReturn(tomorrow);
        when(rstring.getStringFromId(R.string.tomorrow)).thenReturn("tomorrow");
        String text = sut.getText();

        assertThat(text, is("Tomorrow " + getDateAsText(tomorrow)));
    }
    @Test public void getText_ForEventThatTriggersDayAfterTomorrow() {
        when(event.getDate()).thenReturn(dayAfterTomorrow);
        when(rstring.getStringFromId(R.string.day_after_tomorrow)).thenReturn("day after tomorrow");
        String text = sut.getText();

        assertThat(text, is("Day after tomorrow " + getDateAsText(dayAfterTomorrow)));
    }

    @Test public void getText_ForEventThatTriggersNDaysInFuture() {
        LocalDate future = today.plusDays(10);
        when(event.getDate()).thenReturn(future);
        when(rstring.getStringFromId(R.string.in_x_days)).thenReturn("in %1$s days");
        String text = sut.getText();

        assertThat(text, is("In 10 days " + getDateAsText(future)));
    }

    @Test public void onliner () {
        when(event.getDate()).thenReturn(today);
        when(rstring.getStringFromId(R.string.today)).thenReturn("today");
        when(rstring.getFormattedTitle(event)).thenReturn("event's title");
        String text = sut.getOneLiner();

        assertThat(text, is("event's title today " + getDateAsText(today)));
    }

    private String getDateAsText(LocalDate date) {
        return date.dayOfWeek().getAsText() + " " +
                date.getDayOfMonth() + " " + date.monthOfYear().getAsText();
    }
}
