package com.lweynant.yearly.controller;

import com.lweynant.yearly.IDateFormatter;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IAlarm;
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
    private AlarmArchiver sut;

    @Before public void setUp() {
        sut = new AlarmArchiver(alarm, preferences, dateFormatter);
    }

    @Test public void archiveToPreferences() {
        LocalDate date = new LocalDate(2017, Date.JANUARY, 15);
        int hour = 19;
        String formattedTime = "formatted time";
        when(dateFormatter.format(date, hour)).thenReturn(formattedTime);
        sut.scheduleAlarm(date, hour);

        verify(preferences).setStringValue(AlarmArchiver.CURRENT_ALARM, formattedTime);
        verify(alarm).scheduleAlarm(date, hour);
    }
    @Test public void clearFromPreferences() {

        sut.clear();

        verify(preferences).remove(AlarmArchiver.CURRENT_ALARM);
        verify(alarm).clear();
    }

}
