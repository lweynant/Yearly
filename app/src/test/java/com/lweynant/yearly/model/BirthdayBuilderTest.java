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

    @Mock private IValidator validator;
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

    @Test public void testSetName() throws Exception {
        sut.setName("John");
        verify(validator).setName("John");
    }

    @Test public void testSetDay() throws Exception {
        sut.setDay(9);
        verify(validator).setDay(9);
    }
    @Test public void testSetMonth() {
        sut.setMonth(Date.APRIL);
        verify(validator).setMonth(Date.APRIL);
    }
    @Test public void testSetYear() {
        sut.setYear(1000);
        verify(validator).setYear(1000);
    }
    @Test public void testClearYear() {
        sut.clearYear();
        verify(validator).clearYear();
    }
    @Test public void testSetLastName() {
        sut.setLastName("Flinstone");

        //verify
        when(validator.validString("Flinstone")).thenReturn(true);
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);
        verify(bundle).putString(Birthday.KEY_LAST_NAME, "Flinstone");
    }

    @Test public void testBuildMinimalValidBirthday() throws Exception {
        stubValidator("name", Date.APRIL, 20);
        Birthday bd = sut.build();
        assertThat(bd, instanceOf(Birthday.class));
        assertThat(bd, is(birthday("name", Date.APRIL, 20)));
    }
    @Test public void testBuildMinimalValidBirthdayWithID() throws Exception {
        stubValidator("ID", 333, "name", Date.APRIL, 20);
        Birthday bd = sut.build();
        assertThat(bd, instanceOf(Birthday.class));
        assertThat(bd, is(birthday("ID", 333, "name", Date.APRIL, 20)));
    }

    private void stubValidator(String stringID, int id, String name, int month, int day) {
        when(validator.validID()).thenReturn(true);
        when(validator.getID()).thenReturn(id);
        when(validator.getStringID()).thenReturn(stringID);
        stubValidator(name, month, day);
    }

    private void stubValidator(String name, int month, int day) {
        when(validator.validName()).thenReturn(true);
        when(validator.getName()).thenReturn(name);
        when(validator.validMonth()).thenReturn(true);
        when(validator.getMonth()).thenReturn(month);
        when(validator.validDay()).thenReturn(true);
        when(validator.getDay()).thenReturn(day);
    }

    @Test public void testBuildTwiceGivesOtherInstance() throws Exception {
        stubValidator("name", Date.APRIL, 20);
        Birthday bd = sut.build();
        Birthday other = sut.build();
        assertThat(bd, not(sameInstance(other)));
    }

    @Test public void testBuildValidBirthDayWithLastName() throws Exception {
        stubValidator("Joe", Date.APRIL, 22);
        sut.setLastName("Doe");
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Joe", "Doe", Date.APRIL, 22)));
    }

    @Test public void testBuildValidBirthDayWithYear() throws Exception {
        stubValidator("Joe", 2009, Date.FEBRUARY, 15);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Joe", 2009, Date.FEBRUARY, 15)));
    }

    private void stubValidator(String name, int year, int month, int day) {
        stubValidator(name, month, day);
        when(validator.validYear()).thenReturn(true);
        when(validator.getYear()).thenReturn(year);
    }

    @Test public void testArchiveBirthdayWithoutLastNameToBundle() throws Exception {
        stubValidator("Joe", 2009, Date.FEBRUARY, 15);
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);
        verify(archiver).writeValidatorToBundle(validator, bundle);
        verify(bundle, times(1)).remove(BirthdayBuilder.KEY_LAST_NAME);
        verifyNoMoreInteractions(bundle);
    }

    @Test public void testArchiveBirthdayWithLastNameToBundle() throws Exception {
        stubValidator("Joe", 2009, Date.FEBRUARY, 15);
        sut.setLastName("Doe");
        Bundle bundle = mock(Bundle.class);
        when(validator.validString("Doe")).thenReturn(true);
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
        stubValidator("Fred", Date.APRIL, 21);
        sut.set(bundle);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Fred", Date.APRIL, 21)));
    }

    @Test public void testSetFromCompleteBundle() throws Exception {
        Bundle bundle = mock(Bundle.class);
        when(archiver.readValidatorFromBundle(bundle)).thenReturn(validator);
        stubValidator("Fred", 1500, Date.APRIL, 21);

        when(bundle.containsKey(BirthdayBuilder.KEY_LAST_NAME)).thenReturn(true);
        when(bundle.getString(BirthdayBuilder.KEY_LAST_NAME)).thenReturn("Flinstone");
        sut.set(bundle);
        Birthday bd = sut.build();
        assertThat(bd, is(birthday("Fred", "Flinstone", 1500, Date.APRIL, 21)));
    }

}
