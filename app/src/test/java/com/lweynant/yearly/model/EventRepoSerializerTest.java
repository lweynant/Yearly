package com.lweynant.yearly.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUUID;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static java.lang.String.valueOf;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventRepoSerializerTest {

    @Mock
    IClock clock;
    @Mock
    IUUID iuuid;
    private EventRepoSerializer sut;

    @Before
    public void setUp() throws Exception{
        when(clock.now()).thenReturn(new LocalDate(2000, Date.FEBRUARY, 8));
        when(clock.timestamp()).thenReturn("timestamp");
        when(iuuid.getRandomUID()).thenReturn("random id");
        sut = new EventRepoSerializer(clock);
    }
    @Test
    public void testEmpty() throws Exception{
        sut.onCompleted();

        assertThat(sut.isSerialized(), is(true));
        JsonObject json = sut.serialized();
        assertThatJson(json).isObject();
        assertThatJson(json).node(EventRepoSerializer.TYPE).isEqualTo(EventRepoSerializer.class.getCanonicalName());
        assertThatJson(json).node(EventRepoSerializer.VERSION).isStringEqualTo("1.0");
        assertThatJson(json).node(EventRepoSerializer.SERIALIZED_ON).isStringEqualTo("timestamp");
        assertThatJson(json).node(EventRepoSerializer.EVENTS).isArray().ofLength(0);
    }

    @Test
    public void testOneEvent() throws Exception{
        Event event = createEvent(Date.APRIL, 20);
        sut.onNext(event);
        sut.onCompleted();

        assertThat(sut.isSerialized(), is(true));
        JsonObject json = sut.serialized();
        assertThatJson(json).node("events").isArray().ofLength(1);
        assertThatJson(json).node("events[0].month").isEqualTo(Date.APRIL);
    }

    @Test
    public void test2Events() throws Exception{
        sut.onNext(createEvent(Date.AUGUST, 23));
        sut.onNext(createEvent(Date.JANUARY, 3));
        sut.onCompleted();

        assertThat(sut.isSerialized(), is(true));
        JsonObject json = sut.serialized();
        assertThatJson(json).node("events").isArray().ofLength(2);
        assertThatJson(json).node("events[0].month").isEqualTo(Date.AUGUST);
        assertThatJson(json).node("events[1].month").isEqualTo(Date.JANUARY);
    }
    @Test
        public void testDeserialize_prototype() throws Exception{
        sut.onNext(createEvent(Date.AUGUST, 23));
        sut.onNext(createEvent(Date.JANUARY, 3));
        sut.onCompleted();

        assertThat(sut.isSerialized(), is(true));
        JsonObject json = sut.serialized();
        assertThatJson(json).node("events").isArray().ofLength(2);
        assertThatJson(json).node("events[0].month").isEqualTo(Date.AUGUST);
        assertThatJson(json).node("events[1].month").isEqualTo(Date.JANUARY);
        JsonParser parser = new JsonParser();

        JsonObject jsonObject = parser.parse(json.toString()).getAsJsonObject();
        JsonArray eventsArray = jsonObject.getAsJsonArray("events");
        assertThat(eventsArray.size(), is(2));


    }

    @Test
    public void testOnError_Empty() throws Exception{
        sut.onError(new Throwable());

        assertThat(sut.isSerialized(), is(false));
    }


    private Event createEvent(int month, int day) {
        return new Event(month, day, clock, iuuid);

    }
}
