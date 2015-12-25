package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.lweynant.yearly.matchers.IsBirthday.birthday;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BirthdayBuilderTest {

    private BirthdayBuilder sut;
    @Mock
    private IClock clock;
    @Mock
    private IUniqueIdGenerator uniqueIdGenerator;

    @Before
    public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2000, Date.JANUARY, 1));
        sut = new BirthdayBuilder(clock, uniqueIdGenerator);
    }

    @Test
    public void testBuilderNothingSet() throws Exception {
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test
    public void testBuilderOnlyNameSet() throws Exception {
        sut.setName("John");
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test
    public void testBuilderOnlyDaySet() throws Exception {
        sut.setDay(9);
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test
    public void testBuildMinimalValidBirthday() throws Exception {
        sut.setName("name");
        sut.setMonth(Date.APRIL).setDay(20);
        Birthday bd = sut.build();
        assertThat(bd, instanceOf(Birthday.class));
        assertThat(bd, is(birthday("name", Date.APRIL, 20)));
    }
    @Test
    public void testBuildTwiceGivesOtherInstance() throws Exception {
        sut.setName("name");
        sut.setMonth(Date.APRIL).setDay(20);
        Birthday bd = sut.build();
        Birthday other = sut.build();
        assertThat(bd, not(sameInstance(other)));
    }

    @Test
    public void testBuildValidBirthDayWithYear() throws Exception {
        sut.setName("Joe");
        sut.setYear(2009).setMonth(Date.FEBRUARY).setDay(15);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Joe", 2009, Date.FEBRUARY, 15)));
    }



}
