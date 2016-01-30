package com.lweynant.yearly.model;

import android.os.Bundle;

import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IUniqueIdGenerator;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static com.lweynant.yearly.matcher.IsBirthday.birthday;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BirthdayBuilderTest {

    private BirthdayBuilder sut;
    @Mock private IClock clock;
    @Mock private IUniqueIdGenerator uniqueIdGenerator;

    @Before public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2000, Date.JANUARY, 1));
        sut = new BirthdayBuilder(new Validator(), new KeyValueArchiver(new ValidatorFactory()), clock, uniqueIdGenerator);
    }

    @Test public void testBuilderNothingSet() throws Exception {
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test public void testBuilderOnlyNameSet() throws Exception {
        sut.setName("John");
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test public void testBuilderOnlyDaySet() throws Exception {
        sut.setDay(9);
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test public void testBuildMinimalValidBirthday() throws Exception {
        sut.setName("name");
        sut.setMonth(Date.APRIL).setDay(20);
        Birthday bd = sut.build();
        assertThat(bd, instanceOf(Birthday.class));
        assertThat(bd, is(birthday("name", Date.APRIL, 20)));
    }

    @Test public void testBuildTwiceGivesOtherInstance() throws Exception {
        sut.setName("name");
        sut.setMonth(Date.APRIL).setDay(20);
        Birthday bd = sut.build();
        Birthday other = sut.build();
        assertThat(bd, not(sameInstance(other)));
    }

    @Test public void testBuildValidBirthDayWithLastName() throws Exception {
        sut.setName("Joe").setLastName("Doe").setMonth(Date.APRIL).setDay(22);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Joe", "Doe", Date.APRIL, 22)));
    }

    @Test public void testBuildValidBirthDayWithYear() throws Exception {
        sut.setName("Joe");
        sut.setYear(2009).setMonth(Date.FEBRUARY).setDay(15);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Joe", 2009, Date.FEBRUARY, 15)));
    }
    @Test public void testBuildValidBirthDayWithClearYear() throws Exception {
        sut.setName("Joe");
        sut.setYear(2009).setMonth(Date.FEBRUARY).setDay(15);
        sut.clearYear();
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Joe", Date.FEBRUARY, 15)));
    }

    @Test public void testArchiveMinimalBirthdayToBundle() throws Exception {
        sut.setName("Joe").setMonth(Date.DECEMBER).setDay(20);
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);
        verify(bundle, times(1)).putString(BirthdayBuilder.KEY_NAME, "Joe");
        verify(bundle, times(1)).putInt(BirthdayBuilder.KEY_MONTH, Date.DECEMBER);
        verify(bundle, times(1)).putInt(BirthdayBuilder.KEY_DAY, 20);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_YEAR);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_LAST_NAME);
        verifyNoMoreInteractions(bundle);
    }

    @Test public void testArchiveCompleteBirthdayToBundle() throws Exception {
        sut.setName("Joe").setLastName("Doe").setYear(1966).setMonth(Date.DECEMBER).setDay(20);
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);
        verify(bundle, times(1)).putString(BirthdayBuilder.KEY_NAME, "Joe");
        verify(bundle, times(1)).putString(BirthdayBuilder.KEY_LAST_NAME, "Doe");
        verify(bundle, times(1)).putInt(BirthdayBuilder.KEY_YEAR, 1966);
        verify(bundle, times(1)).putInt(BirthdayBuilder.KEY_MONTH, Date.DECEMBER);
        verify(bundle, times(1)).putInt(BirthdayBuilder.KEY_DAY, 20);
        verifyNoMoreInteractions(bundle);
    }

    @Test public void testArchiveEmptyToBundle() {
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_NAME);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_LAST_NAME);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_YEAR);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_MONTH);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_DAY);
        verifyNoMoreInteractions(bundle);
    }

    @Test public void testSetFromEmptyBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        sut.set(bundle);
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test public void testSetFromMinimalBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        when(bundle.containsKey(BirthdayBuilder.KEY_NAME)).thenReturn(true);
        when(bundle.getString(BirthdayBuilder.KEY_NAME)).thenReturn("Fred");
        when(bundle.containsKey(BirthdayBuilder.KEY_MONTH)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_MONTH)).thenReturn(Date.APRIL);
        when(bundle.containsKey(BirthdayBuilder.KEY_DAY)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_DAY)).thenReturn(21);
        sut.set(bundle);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Fred", Date.APRIL, 21)));
    }

    @Test public void testSetFromCompleteBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        when(bundle.containsKey(BirthdayBuilder.KEY_NAME)).thenReturn(true);
        when(bundle.getString(BirthdayBuilder.KEY_NAME)).thenReturn("Fred");
        when(bundle.containsKey(BirthdayBuilder.KEY_LAST_NAME)).thenReturn(true);
        when(bundle.getString(BirthdayBuilder.KEY_LAST_NAME)).thenReturn("Flinstone");
        when(bundle.containsKey(BirthdayBuilder.KEY_YEAR)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_YEAR)).thenReturn(1500);
        when(bundle.containsKey(BirthdayBuilder.KEY_MONTH)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_MONTH)).thenReturn(Date.APRIL);
        when(bundle.containsKey(BirthdayBuilder.KEY_DAY)).thenReturn(true);
        when(bundle.getInt(BirthdayBuilder.KEY_DAY)).thenReturn(21);
        sut.set(bundle);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Fred", "Flinstone", 1500, Date.APRIL, 21)));
    }

}
