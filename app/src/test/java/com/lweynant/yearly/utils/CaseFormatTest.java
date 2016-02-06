package com.lweynant.yearly.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CaseFormatTest {

    @Test public void capitalizeFirstLetter() {
        String text = CaseFormat.capitalizeFirstLetter("first letter");
        assertThat(text, is("First letter"));
    }
    @Test public void capitaliseFirstLetterWhenItIsAllreadyCapital() {
        String text = CaseFormat.capitalizeFirstLetter("First letter");
        assertThat(text, is("First letter"));
    }
    @Test public void capitalizeFirstLetterOnNullString() {
        String text = CaseFormat.capitalizeFirstLetter(null);
        assertNull(text);
    }
    @Test public void capitalizeFirstLetterOnEmptyString() {
        String text = CaseFormat.capitalizeFirstLetter("");
        assertThat(text, is(""));
    }
    @Test public void uncapitalizeFirstLetter() {
        String text = CaseFormat.uncapitalizeFirstLetter("First letter");
        assertThat(text, is("first letter"));
    }
    @Test public void uncapitaliseFirstLetterWhenItIsAllreadyLower() {
        String text = CaseFormat.uncapitalizeFirstLetter("first letter");
        assertThat(text, is("first letter"));
    }
    @Test public void uncapitalizeFirstLetterOnNullString() {
        String text = CaseFormat.uncapitalizeFirstLetter(null);
        assertNull(text);
    }
    @Test public void uncapitalizeFirstLetterOnEmptyString() {
        String text = CaseFormat.uncapitalizeFirstLetter("");
        assertThat(text, is(""));
    }

}
