package com.lweynant.yearly.controller;

import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.AlarmArchiver;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlarmArchiverTest {
    @Mock IAlarm alarm;
    @Mock IPreferences preferences;
    @Mock IDateFormatter dateFormatter;
    @Mock IClock clock;
    private AlarmArchiver sut;

    @Before public void setUp() {
        sut = new AlarmArchiver(alarm, preferences, dateFormatter, clock);
    }

    @Test public void archiveToPreferences() {
        LocalDate date = new LocalDate(2017, Date.JANUARY, 15);
        LocalDate now = new LocalDate(2017, Date.JANUARY, 10);
        int hour = 19;
        String formattedDate = "formatted date";
        when(dateFormatter.format(date, hour)).thenReturn(formattedDate);
        String formattedNow = "formatted now";
        when(dateFormatter.format(clock)).thenReturn(formattedNow);
        sut.scheduleAlarm(date, hour);

        verify(preferences).setStringValue(AlarmArchiver.CURRENT_ALARM, formattedDate);
        verify(preferences).setStringValue(AlarmArchiver.ALARM_SET_AT, formattedNow);
        verify(alarm).scheduleAlarm(date, hour);
    }
    @Test public void clearFromPreferences() {

        sut.clear();

        verify(preferences).remove(AlarmArchiver.CURRENT_ALARM);
        verify(preferences).remove(AlarmArchiver.ALARM_SET_AT);
        verify(alarm).clear();
    }

}
