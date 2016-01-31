package com.lweynant.yearly.model;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ValidatorTest {

    private Validator sut;

    @Before public void setUp() {
        sut = new Validator();
    }

    @Test public void testEmptyValidator() {
        assertThat(sut.validName(), is(false));
        assertThat(sut.validYear(), is(false));
        assertThat(sut.validMonth(), is(false));
        assertThat(sut.validDay(), is(false));
    }

    @Test public void testSetValidName() {
        sut.setName("Fred");
        assertThat(sut.validName(), is(true));
        assertThat(sut.getName(), is("Fred"));
    }

    @Test public void testSetInvalidName() {
        sut.setName("");
        assertThat(sut.validName(), is(false));
    }

    @Test public void testSetYear() {
        sut.setYear(1200);
        assertThat(sut.validYear(), is(true));
        assertThat(sut.getYear(), is((1200)));
    }
    @Test public void testClearYear() {
        sut.setYear(2300);
        sut.clearYear();
        assertThat(sut.validYear(), is(false));
    }
    @Test public void setValidMonth() {
        sut.setMonth(Date.APRIL);
        assertThat(sut.validMonth(), is(true));
        assertThat(sut.getMonth(), is((Date.APRIL)));
    }
    @Test public void setInvalidMonth_TooLow() {
        //noinspection ResourceType
        sut.setMonth(0);
        assertThat(sut.validMonth(), is(false));
    }
    @Test public void setInvalidMonth_TooHigh() {
        //noinspection ResourceType
        sut.setMonth(13);
        assertThat(sut.validMonth(), is(false));
    }
    @Test public void setValidDay() {
        sut.setDay(20);
        assertThat(sut.validDay(), is(true));
        assertThat(sut.getDay(), is(20));
    }
    @Test public void setInvalidDay_TooLow() {
        sut.setDay(0);
        assertThat(sut.validDay(), is(false));
    }
    @Test public void setInvalidDay_TooHigh() {
        sut.setDay(32);
        assertThat(sut.validDay(), is(false));
    }

    @Test public void validStringOnValidString() {
        boolean valid = sut.validString("Doe");
        assertThat(valid, is(true));
    }
    @Test public void validStringOnEmptyString() {
        boolean valid = sut.validString("");
        assertThat(valid, is(false));
    }
    @Test public void validStringOnNullString() {
        boolean valid = sut.validString(null);
        assertThat(valid, is(false));
    }
}
