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

    private final Validator validator = new Validator();
    private BirthdayBuilder sut;
    @Mock private IClock clock;
    @Mock private IUniqueIdGenerator uniqueIdGenerator;
    @Mock private IKeyValueArchiver archiver;

    @Before public void setUp() throws Exception {
        when(clock.now()).thenReturn(new LocalDate(2000, Date.JANUARY, 1));
        sut = new BirthdayBuilder(validator, archiver, clock, uniqueIdGenerator);
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

    @Test public void testArchiveBirthdayWithoutLastNameToBundle() throws Exception {
        sut.setName("Joe").setMonth(Date.DECEMBER).setDay(20);
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);
        verify(archiver).writeValidatorToBundle(validator, bundle);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_LAST_NAME);
        verifyNoMoreInteractions(bundle);
    }

    @Test public void testArchiveBirthdayWithLastNameToBundle() throws Exception {
        sut.setName("Joe").setLastName("Doe").setYear(1966).setMonth(Date.DECEMBER).setDay(20);
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);

        verify(archiver).writeValidatorToBundle(validator, bundle);
        verify(bundle, times(1)).putString(BirthdayBuilder.KEY_LAST_NAME, "Doe");
        verifyNoMoreInteractions(bundle);
    }


    @Test public void testSetFromBundleWithoutLastName() throws Exception {
        Bundle bundle = mock(Bundle.class);
        when(archiver.readValidatorFromBundle(bundle)).thenReturn(validator);
        sut.set(bundle);

        verify(archiver).readValidatorFromBundle(bundle);
        Birthday bd = sut.build();
        assertNull(bd);
    }

    @Test public void testSetFromMinimalBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        when(archiver.readValidatorFromBundle(bundle)).thenReturn(validator);
        validator.setName("Fred");
        validator.setMonth(Date.APRIL);
        validator.setDay(21);
        sut.set(bundle);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Fred", Date.APRIL, 21)));
    }

    @Test public void testSetFromCompleteBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        when(archiver.readValidatorFromBundle(bundle)).thenReturn(validator);
        validator.setName("Fred");
        validator.setYear(1500);
        validator.setMonth(Date.APRIL);
        validator.setDay(21);

        when(bundle.containsKey(BirthdayBuilder.KEY_LAST_NAME)).thenReturn(true);
        when(bundle.getString(BirthdayBuilder.KEY_LAST_NAME)).thenReturn("Flinstone");
        sut.set(bundle);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Fred", "Flinstone", 1500, Date.APRIL, 21)));
    }

}
