package com.lweynant.yearly.model;

import com.lweynant.yearly.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(MockitoJUnitRunner.class)
public class UUIDTest {

    @Test
    public void testUUID() throws Exception {
        UUID sut = new UUID();

        int hash = sut.hashCode("3997b6e5-218b-4b96-b44a-5fbdf9c32295");
        assertThat(hash, is(1435861083));

    }
}
