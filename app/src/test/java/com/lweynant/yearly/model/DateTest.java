package com.lweynant.yearly.model;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class DateTest {

    @Test
    public void testDayBeforeEventAtMiddleOfMonth() throws Exception{
        LocalDate date = new LocalDate(2015, Date.MARCH, 20);

        LocalDate dayBefore = date.minusDays(1);
        assertThat(dayBefore.getDayOfMonth(), is(19));
        assertThat(dayBefore.getMonthOfYear(), is(Date.MARCH));
    }
    @Test
    public void testDayBeforeEventAtFirstDayOfMonth() throws Exception{
        LocalDate date = new LocalDate(2015, Date.MARCH, 1);

        LocalDate dayBefore = date.minusDays(1);
        assertThat(dayBefore.getDayOfMonth(), is(28));
        assertThat(dayBefore.getMonthOfYear(), is(Date.FEBRUARY));
    }
}
