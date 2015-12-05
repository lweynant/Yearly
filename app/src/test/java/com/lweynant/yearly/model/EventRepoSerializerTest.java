package com.lweynant.yearly.model;

import com.lweynant.yearly.util.IClock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class EventRepoSerializerTest {

    @Mock
    IClock clock;
    private EventRepoSerializer sut;

    @Before
    public void setUp() throws Exception{
        sut = new EventRepoSerializer();
    }
    @Test
    public void testEmpty() throws Exception{
        sut.onCompleted();

        String json = sut.serialized();
        assertThatJson(json).isArray().ofLength(0);
    }

    @Test
    public void testOneEvent() throws Exception{
        Event event = createEvent(Date.APRIL, 20);
        sut.onNext(event);
        sut.onCompleted();

        String json = sut.serialized();
        assertThatJson(json).isArray().ofLength(1);
        assertThatJson(json).node("[0].month").isEqualTo(Date.APRIL);
        assertThatJson(json).node("[0].day").isEqualTo(20);
    }

    @Test
    public void test2Events() throws Exception{
        sut.onNext(createEvent(Date.AUGUST, 23));
        sut.onNext(createEvent(Date.JANUARY, 3));
        sut.onCompleted();

        String json = sut.serialized();
        assertThatJson(json).isArray().ofLength(2);
        assertThatJson(json).node("[0].month").isEqualTo(Date.AUGUST);
        assertThatJson(json).node("[1].month").isEqualTo(Date.JANUARY);
    }

    private Event createEvent(int month, int day) {
        return new Event(month, day, clock);

    }
}
