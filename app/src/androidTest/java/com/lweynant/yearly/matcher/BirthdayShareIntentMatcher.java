package com.lweynant.yearly.matcher;

import android.content.Intent;
import android.os.Bundle;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class BirthdayShareIntentMatcher extends TypeSafeMatcher<Intent> {

    private Matcher<Intent> matcher;

    public BirthdayShareIntentMatcher(Matcher<Intent> matcher) {

        this.matcher = matcher;
    }

    static public BirthdayShareIntentMatcher matchShareIntentWrappedInChooser(Matcher<Intent> matcher) {
        return new BirthdayShareIntentMatcher(matcher);
    }
    @Override protected boolean matchesSafely(Intent chooserIntent) {
        if (chooserIntent.hasExtra(Intent.EXTRA_INTENT)){
            Intent shareIntent = chooserIntent.getParcelableExtra(Intent.EXTRA_INTENT);
            return matcher.matches(shareIntent);
        }
        return false;
    }

    @Override public void describeTo(Description description) {
        matcher.describeTo(description);
    }
}
