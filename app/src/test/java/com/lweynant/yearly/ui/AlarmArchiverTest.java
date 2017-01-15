package com.lweynant.yearly.ui;

import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IPreferences;
import com.lweynant.yearly.platform.ITimeConvertor;
import com.lweynant.yearly.utils.AlarmArchiver;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AlarmArchiverTest {
    @Mock IAlarm alarm;
    @Mock IPreferences preferences;
    @Mock ITimeConvertor timeConvertor;
    private AlarmArchiver sut;

    @Before public void setUp() {
        sut = new AlarmArchiver(alarm, preferences, timeConvertor);
    }

    @Test public void archiveToPreferences() {
        LocalDate date = new LocalDate(2017, Date.JANUARY, 15);
        int hour = 19;
        String time = "time";
        when(timeConvertor.convert(date, hour)).thenReturn(time);
        sut.scheduleAlarm(date, hour);

        verify(preferences).setStringValue(AlarmArchiver.CURRENT_ALARM, time);
        verify(alarm).scheduleAlarm(date, hour);
    }
    @Test public void clearFromPreferences() {

        sut.clear();

        verify(preferences).remove(AlarmArchiver.CURRENT_ALARM);
        verify(alarm).clear();
    }

}
