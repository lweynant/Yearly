package com.lweynant.yearly.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
@RunWith(MockitoJUnitRunner.class)
public class BirthdayTest {

    private static final String BIRTHDAY_TITLE = "%1$s's birthday";
    @Mock
    IClock clock;
    @Mock
    IUniqueIdGenerator uniqueIdGenerator;
    @Before
    public void setUp(){
        when(clock.now()).thenReturn(new LocalDate(2015, 8, 9));
    }
    @Test
    public void getTitle_ValidBirthday_ReturnsValidTitle() throws Exception{

        Birthday bd = new Birthday("John", Date.APRIL, 23, clock, uniqueIdGenerator);
        assertThat(bd.getName(), is("John"));
    }

    @Test
    public  void getDate_ValidBirthday_ReturnsValidDayAndMonth() throws Exception{
        int day = 23;
        @Date.Month int month = Date.FEBRUARY;
        Birthday bd = new Birthday("John", month, day, clock, uniqueIdGenerator);
        assertThat(bd.getDate().getDayOfMonth(), is(day));
        assertThat(bd.getDate().getMonthOfYear(), is(month));
    }

    @Test
    public void getDate_SameAsNow() throws Exception{
        LocalDate now = new LocalDate(2013, 7, 23);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Fred", now.getMonthOfYear(), now.getDayOfMonth(), clock, uniqueIdGenerator);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now));
    }

    @Test
    public void getDate_AfterNow() throws Exception{
        LocalDate now = new LocalDate(2014, 6, 5);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", now.getMonthOfYear(), now.getDayOfMonth() + 1 , clock, uniqueIdGenerator);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.plusDays(1)));
    }
    @Test
    public void getDate_BeforeNow() throws Exception{
        LocalDate now = new LocalDate(2014, 6, 5);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", now.getMonthOfYear(), now.getDayOfMonth() - 1 , clock, uniqueIdGenerator);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.minusDays(1).plusYears(1)));
    }
    @Test
    public void getDate_FirstDayOfYearAskedOnLastDayOfYear() throws Exception{
        LocalDate now = new LocalDate(2014, 12, 31);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", Date.JANUARY, 1 , clock, uniqueIdGenerator);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(now.plusDays(1)));
    }
    @Test
    public void getDate_LastDayOfYearAskedOnFirstDayOfYear() throws Exception{
        LocalDate now = new LocalDate(2014, 1, 1);
        when(clock.now()).thenReturn(now);
        Birthday bd = new Birthday("Joe", Date.DECEMBER, 31 , clock, uniqueIdGenerator);
        LocalDate eventDate = bd.getDate();
        assertThat(eventDate, is(new LocalDate(2014, 12, 31)));
    }

    @Test
    public void compareTo_NowIsAfter() throws Exception {
        LocalDate now = new LocalDate(2014, 7, 15);
        when(clock.now()).thenReturn(now);
        Birthday joe =  new Birthday("joe", Date.MARCH, 4, clock, uniqueIdGenerator);
        Birthday fred = new Birthday("fred", Date.NOVEMBER, 5, clock, uniqueIdGenerator);

        assertThat(joe.compareTo(fred), is(1));
    }
    @Test
    public void compareTo_NowIsBefore() throws Exception {
        LocalDate now = new LocalDate(2014, 1, 15);
        when(clock.now()).thenReturn(now);
        Birthday joe =  new Birthday("joe", Date.MARCH, 4, clock, uniqueIdGenerator);
        Birthday fred = new Birthday("fred", Date.NOVEMBER, 5, clock, uniqueIdGenerator);

        assertThat(joe.compareTo(fred), is(-1));
    }

    @Test
    public void testSerializeBirthday() throws Exception{
        Birthday bd = new Birthday("Mine", Date.FEBRUARY, 8, clock, uniqueIdGenerator);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);
        assertThatJson(json).node(Birthday.KEY_NAME).isEqualTo("Mine");
        assertThatJson(json).node(Birthday.KEY_MONTH).isEqualTo(Date.FEBRUARY);
        assertThatJson(json).node(Birthday.KEY_DAY).isEqualTo(8);
        assertThatJson(json).node(Birthday.KEY_YEAR_OF_BIRTH).isAbsent();
        assertThatJson(json).node(Birthday.KEY_TYPE).isEqualTo(Birthday.class.getCanonicalName());
        assertThatJson(json).node(Birthday.KEY_NBR_DAYS_FOR_NOTIFICATION).isEqualTo(2);
    }
    @Test
    public void testSerializeBirthday_WithValidYearOfBirth() throws Exception{
        Birthday bd = new Birthday("Mine", 1966, Date.FEBRUARY, 8, clock, uniqueIdGenerator);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);
        assertThatJson(json).node(Birthday.KEY_NAME).isEqualTo("Mine");
        assertThatJson(json).node(Birthday.KEY_MONTH).isEqualTo(Date.FEBRUARY);
        assertThatJson(json).node(Birthday.KEY_DAY).isEqualTo(8);
        assertThatJson(json).node(Birthday.KEY_YEAR_OF_BIRTH).isEqualTo(1966);
        assertThatJson(json).node(Birthday.KEY_TYPE).isEqualTo(Birthday.class.getCanonicalName());
    }

    @Test
    public void testDeserializeBirthDay() throws Exception{

        Birthday bd = new Birthday("Mine", Date.FEBRUARY, 8, clock, uniqueIdGenerator);
        GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation();
        Gson gson = builder.create();
        String json = gson.toJson(bd);

        builder.registerTypeAdapter(Birthday.class, new BirthdayInstanceCreator(clock, uniqueIdGenerator));
        gson = builder.create();
        Birthday readBirthday = gson.fromJson(json, Birthday.class);
        assertThat(readBirthday.getName(), is("Mine"));
        assertThat(readBirthday.getType(), is(Birthday.class.getCanonicalName()));
    }
}
