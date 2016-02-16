package com.lweynant.yearly.model;

import android.os.Bundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KeyValueArchiverTest {

    @Mock ValidatorFactory validatorFactory;
    @Mock Bundle bundle;
    private KeyValueArchiver sut;

    @Before public void setUp() {
        when(validatorFactory.create()).thenReturn(mock(IValidator.class));
        sut = new KeyValueArchiver(validatorFactory);
    }

    @Test public void testWriteEmptyValidatorToBundle() {
        IValidator validator = mock(IValidator.class);
        sut.writeValidatorToBundle(validator, bundle);

        verify(bundle).remove(IKeyValueArchiver.KEY_NAME);
        verify(bundle).remove(IKeyValueArchiver.KEY_YEAR);
        verify(bundle).remove(IKeyValueArchiver.KEY_MONTH);
        verify(bundle).remove(IKeyValueArchiver.KEY_DAY);
        verify(bundle).remove(IKeyValueArchiver.KEY_STRING_ID);
        verify(bundle).remove(IKeyValueArchiver.KEY_ID);
        verifyNoMoreInteractions(bundle);
    }
    @Test public void testWriteValidatorWithNameToBundle() {
        IValidator validator = mock(IValidator.class);
        when(validator.validName()).thenReturn(true);
        when(validator.getName()).thenReturn("Joe");
        sut.writeValidatorToBundle(validator, bundle);

        verify(bundle).putString(IKeyValueArchiver.KEY_NAME, "Joe");
        verify(bundle).remove(IKeyValueArchiver.KEY_YEAR);
        verify(bundle).remove(IKeyValueArchiver.KEY_MONTH);
        verify(bundle).remove(IKeyValueArchiver.KEY_DAY);
        verify(bundle).remove(IKeyValueArchiver.KEY_STRING_ID);
        verify(bundle).remove(IKeyValueArchiver.KEY_ID);
        verifyNoMoreInteractions(bundle);
    }
    @Test public void testWriteValidatorWithYearToBundle() {
        IValidator validator = mock(IValidator.class);
        when(validator.validYear()).thenReturn(true);
        when(validator.getYear()).thenReturn(2016);
        sut.writeValidatorToBundle(validator, bundle);

        verify(bundle).remove(IKeyValueArchiver.KEY_NAME);
        verify(bundle).putInt(IKeyValueArchiver.KEY_YEAR, 2016);
        verify(bundle).remove(IKeyValueArchiver.KEY_MONTH);
        verify(bundle).remove(IKeyValueArchiver.KEY_DAY);
        verify(bundle).remove(IKeyValueArchiver.KEY_STRING_ID);
        verify(bundle).remove(IKeyValueArchiver.KEY_ID);

        verifyNoMoreInteractions(bundle);
    }
    @Test public void testWriteValidatorWithMonthToBundle() {
        IValidator validator = mock(IValidator.class);
        when(validator.validMonth()).thenReturn(true);
        when(validator.getMonth()).thenReturn(Date.APRIL);
        sut.writeValidatorToBundle(validator, bundle);

        verify(bundle).remove(IKeyValueArchiver.KEY_NAME);
        verify(bundle).remove(IKeyValueArchiver.KEY_YEAR);
        verify(bundle).putInt(IKeyValueArchiver.KEY_MONTH, Date.APRIL);
        verify(bundle).remove(IKeyValueArchiver.KEY_DAY);
        verify(bundle).remove(IKeyValueArchiver.KEY_STRING_ID);
        verify(bundle).remove(IKeyValueArchiver.KEY_ID);

        verifyNoMoreInteractions(bundle);
    }
    @Test public void testWriteValidatorWithIDToBundle() {
        IValidator validator = mock(IValidator.class);
        when(validator.validID()).thenReturn(true);
        when(validator.getID()).thenReturn(666);
        when(validator.getStringID()).thenReturn("id");
        sut.writeValidatorToBundle(validator, bundle);

        verify(bundle).remove(IKeyValueArchiver.KEY_NAME);
        verify(bundle).remove(IKeyValueArchiver.KEY_YEAR);
        verify(bundle).remove(IKeyValueArchiver.KEY_MONTH);
        verify(bundle).remove(IKeyValueArchiver.KEY_DAY);
        verify(bundle).putString(IKeyValueArchiver.KEY_STRING_ID, "id");
        verify(bundle).putInt(IKeyValueArchiver.KEY_ID, 666);

        verifyNoMoreInteractions(bundle);
    }
    @Test public void testWriteValidatorWithDayToBundle() {
        IValidator validator = mock(IValidator.class);
        when(validator.validDay()).thenReturn(true);
        when(validator.getDay()).thenReturn(24);
        sut.writeValidatorToBundle(validator, bundle);

        verify(bundle).remove(IKeyValueArchiver.KEY_NAME);
        verify(bundle).remove(IKeyValueArchiver.KEY_YEAR);
        verify(bundle).remove(IKeyValueArchiver.KEY_MONTH);
        verify(bundle).remove(IKeyValueArchiver.KEY_STRING_ID);
        verify(bundle).remove(IKeyValueArchiver.KEY_ID);

        verify(bundle).putInt(IKeyValueArchiver.KEY_DAY, 24);
        verifyNoMoreInteractions(bundle);
    }

    @Test public void writeEmptyBundleToValidator() {
        IValidator validator = sut.readValidatorFromBundle(bundle);

        verifyZeroInteractions(validator);
    }

    @Test public void writeBundleWithNameToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_NAME)).thenReturn(true);
        when(bundle.getString(IKeyValueArchiver.KEY_NAME)).thenReturn("Fred");

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verify(validator).setName("Fred");
    }
    @Test public void writeBundleWithIdToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_STRING_ID)).thenReturn(true);
        when(bundle.getString(IKeyValueArchiver.KEY_STRING_ID)).thenReturn("ID");
        when(bundle.containsKey(IKeyValueArchiver.KEY_ID)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_ID)).thenReturn(888);

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verify(validator).setID("ID", 888);
    }
    @Test public void writeBundleWithStringIdOnlyToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_STRING_ID)).thenReturn(true);
        when(bundle.getString(IKeyValueArchiver.KEY_STRING_ID)).thenReturn("ID");

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verifyZeroInteractions(validator);
    }
    @Test public void writeBundleWithIntIdOnlyToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_ID)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_ID)).thenReturn(888);

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verifyZeroInteractions(validator);
    }

    @Test public void writeBundleWithYearToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_YEAR)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_YEAR)).thenReturn(2000);

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verify(validator).setYear(2000);
    }
    @Test public void writeBundleWithMonthToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_MONTH)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_MONTH)).thenReturn(Date.DECEMBER);

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verify(validator).setMonth(Date.DECEMBER);
    }
    @Test public void writeBundleWithDayToValidator() {
        when(bundle.containsKey(IKeyValueArchiver.KEY_DAY)).thenReturn(true);
        when(bundle.getInt(IKeyValueArchiver.KEY_DAY)).thenReturn(26);

        IValidator validator = sut.readValidatorFromBundle(bundle);

        verify(validator).setDay(26);
    }

}
