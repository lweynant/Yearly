package com.lweynant.yearly.matcher;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

public class CollapsingToolBarTitleMatcher {
    public static ViewInteraction matchToolbarTitle(Matcher<String> stringMatcher) {
        return onView(isAssignableFrom(CollapsingToolbarLayout.class))
                .check(matches(withToolbarTitle(stringMatcher)));
    }

    private static Matcher<Object> withToolbarTitle(final Matcher<String> textMatcher) {
        return new BoundedMatcher<Object, CollapsingToolbarLayout>(CollapsingToolbarLayout.class) {
            @Override public boolean matchesSafely(CollapsingToolbarLayout toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }

}
