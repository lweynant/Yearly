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

import static com.lweynant.yearly.matcher.IsEvent.event;
import static junit.framework.Assert.assertNull;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventBuilderTest {

    private EventBuilder sut;
    @Mock private IValidator validator;
    @Mock private IKeyValueArchiver archiver;
    @Mock private IClock clock;
    @Mock private IUniqueIdGenerator idGenerator;
    private LocalDate today;

    @Before public void setUp() {
        today = new LocalDate(2000, Date.FEBRUARY, 8);
        when(clock.now()).thenReturn(today);
        sut = new EventBuilder(validator, archiver, clock, idGenerator);
    }

    @Test public void buildNothingSet() {
        IEvent event = sut.build();
        assertNull(event);
    }

    @Test public void setName() {
        EventBuilder builder = sut.setName("Joe");
        assertThat(builder, is(sut));
        verify(validator).setName("Joe");
    }

    @Test public void setYear() {
        EventBuilder builder = sut.setYear(1000);
        assertThat(builder, is(sut));
        verify(validator).setYear(1000);
    }

    @Test public void clearYear() {
        EventBuilder builder = sut.clearYear();
        assertThat(builder, is(sut));
        verify(validator).clearYear();
    }

    @Test public void setMonth() {
        EventBuilder builder = sut.setMonth(Date.APRIL);
        assertThat(builder, is(sut));
        verify(validator).setMonth(Date.APRIL);
    }

    @Test public void setDay() {
        EventBuilder builder = sut.setDay(23);
        assertThat(builder, is(sut));
        verify(validator).setDay(23);
    }
    @Test public void buildMinimalValidEvent() throws Exception {
        stubValidator("name", Date.APRIL, 20);
        IEvent event = sut.build();
        assertThat(event, instanceOf(Event.class));
        assertThat(event, is(event("name", Date.APRIL, 20)));
    }
    @Test public void buildTwiceGivesOtherInstance() throws Exception {
        stubValidator("name", Date.APRIL, 20);
        IEvent event = sut.build();
        IEvent other = sut.build();
        assertThat(event, not(sameInstance(other)));
    }

    @Test public void buildWithInvalidName() {
        stubValidator("Joe", Date.APRIL, 23);
        when(validator.validName()).thenReturn(false);

        IEvent event = sut.build();
        assertNull(event);
    }
    @Test public void buildWithInvalidMonth() {
        stubValidator("Joe", Date.APRIL, 23);
        when(validator.validMonth()).thenReturn(false);

        IEvent event = sut.build();
        assertNull(event);
    }
    @Test public void buildWithInvalidDay() {
        stubValidator("Joe", Date.APRIL, 23);
        when(validator.validDay()).thenReturn(false);

        IEvent event = sut.build();
        assertNull(event);
    }
    @Test public void buildWithInvalidYear() {
        stubValidator("Joe", Date.APRIL, 23);
        when(validator.validYear()).thenReturn(false);

        IEvent event = sut.build();
        assertThat(event, instanceOf(Event.class));
        assertThat(event, is(event("Joe", Date.APRIL, 23)));
    }
    @Test public void buildWithValidYear() {
        stubValidator("Joe", Date.APRIL, 23);
        when(validator.validYear()).thenReturn(true);
        when(validator.getYear()).thenReturn(2000);

        IEvent event = sut.build();
        assertThat(event, instanceOf(Event.class));
        assertThat(event, is(event("Joe", 2000, Date.APRIL, 23)));
    }

    @Test public void archiveToBundle() {
        Bundle bundle = mock(Bundle.class);
        sut.archiveTo(bundle);

        verify(archiver).writeValidatorToBundle(validator, bundle);
    }

    @Test public void setEmptyBundle() {
        Bundle bundle = mock(Bundle.class);
        IValidator newValidator = mock(IValidator.class);
        when(archiver.readValidatorFromBundle(bundle)).thenReturn(newValidator);
        sut.set(bundle);

        IEvent event = sut.build();
        assertNull(event);
    }
    @Test public void setCompleteBundle() {
        Bundle bundle = mock(Bundle.class);
        IValidator newValidator = mock(IValidator.class);
        when(archiver.readValidatorFromBundle(bundle)).thenReturn(newValidator);
        stubValidator(newValidator, "Joe", Date.APRIL, 23);
        sut.set(bundle);

        IEvent event = sut.build();
        assertThat(event, is(event("Joe", Date.APRIL, 23)));
    }


    private void stubValidator(IValidator validator, String name, int month, int day) {
        when(validator.validName()).thenReturn(true);
        when(validator.getName()).thenReturn(name);
        when(validator.validMonth()).thenReturn(true);
        when(validator.getMonth()).thenReturn(month);
        when(validator.validDay()).thenReturn(true);
        when(validator.getDay()).thenReturn(day);
    }
    private void stubValidator(String name, int month, int day) {
        stubValidator(validator, name, month, day);
    }

}
