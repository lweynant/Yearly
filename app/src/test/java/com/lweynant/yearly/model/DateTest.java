package com.lweynant.yearly.model;

import org.joda.time.LocalDate;
import org.joda.time.Period;
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

    @Test
    public void testDays() throws Exception{
        LocalDate from = new LocalDate(2015, Date.APRIL, 1);
        LocalDate to = from.plusDays(3);
        LocalDate date1 = new LocalDate(2015, Date.APRIL, 3);
        LocalDate date2 = new LocalDate(2015, Date.APRIL, 4);
        assertThat(date1.isAfter(from), is(true));
        assertThat(date1.isBefore(to), is(true));

    }

    @Test
    public void testToString() throws Exception{
        LocalDate date = new LocalDate(2015, Date.FEBRUARY, 20);
        assertThat(date.toString("dd/MM"), is("20/02"));
    }
}
