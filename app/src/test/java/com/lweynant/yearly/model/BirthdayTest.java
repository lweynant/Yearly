package com.lweynant.yearly.model;


import com.lweynant.yearly.IRString;
import com.lweynant.yearly.R;
import com.lweynant.yearly.util.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class BirthdayTest {

    private static final String BIRTHDAY_TITLE = "%1$s's birthday";
    @Mock
    IRString rstring;
    @Mock
    IClock clock;
    @Before
    public void setUp(){
        when(clock.now()).thenReturn(new LocalDate(2015, 8, 9));
    }
    @Test
    public void getTitle_ValidBirthday_ReturnsValidTitle() throws Exception{
        when(rstring.getStringFromId(R.string.birthday_from)).thenReturn(BIRTHDAY_TITLE);

        Birthday bd = new Birthday("John", Date.APRIL, 23, clock, rstring);
        assertThat(bd.getTitle(), is("John's birthday"));
    }

    @Test
    public  void getDate_ValidBirthday_ReturnsValidDayAndMonth() throws Exception{
        int day = 23;
        @Date.Month int month = Date.FEBRUARY;
        Birthday bd = new Birthday("John", month, day, clock,rstring);
        assertThat(bd.getDate().getDayOfMonth(), is(day));
        assertThat(bd.getDate().getMonthOfYear(), is(month));
    }

    @Test
    public void getDate_SameAsNow() throws Exception{
        LocalDate now = new LocalDate(2013, 7, 23);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Fred", now.getMonthOfYear(), now.getDayOfMonth(), clock, rstring);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now));
    }

    @Test
    public void getDate_AfterNow() throws Exception{
        LocalDate now = new LocalDate(2014, 6, 5);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", now.getMonthOfYear(), now.getDayOfMonth() + 1 , clock, rstring);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.plusDays(1)));
    }
    @Test
    public void getDate_BeforeNow() throws Exception{
        LocalDate now = new LocalDate(2014, 6, 5);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", now.getMonthOfYear(), now.getDayOfMonth() - 1 , clock, rstring);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.minusDays(1).plusYears(1)));
    }
    @Test
    public void getDate_FirstDayOfYearAskedOnLastDayOfYear() throws Exception{
        LocalDate now = new LocalDate(2014, 12, 31);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", Date.JANUARY, 1 , clock, rstring);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.plusDays(1)));
    }
    @Test
    public void getDate_LastDayOfYearAskedOnFirstDayOfYear() throws Exception{
        LocalDate now = new LocalDate(2014, 1, 1);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", Date.DECEMBER, 31 , clock, rstring);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(new LocalDate(2014, 12, 31)));
    }

    @Test
    public void compareTo_NowIsAfter() throws Exception {
        LocalDate now = new LocalDate(2014, 7, 15);
        when(clock.now()).thenReturn(now);
        Birthday joe =  new Birthday("joe", Date.MARCH, 4, clock, rstring);
        Birthday fred = new Birthday("fred", Date.NOVEMBER, 5, clock, rstring);

        assertThat(joe.compareTo(fred), is(1));
    }
    @Test
    public void compareTo_NowIsBefore() throws Exception {
        LocalDate now = new LocalDate(2014, 1, 15);
        when(clock.now()).thenReturn(now);
        Birthday joe =  new Birthday("joe", Date.MARCH, 4, clock, rstring);
        Birthday fred = new Birthday("fred", Date.NOVEMBER, 5, clock, rstring);

        assertThat(joe.compareTo(fred), is(-1));
    }
}
