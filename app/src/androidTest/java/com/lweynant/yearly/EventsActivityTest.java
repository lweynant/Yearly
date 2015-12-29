package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.JsonObject;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoFileAccessor;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.util.Clock;
import com.lweynant.yearly.util.ClockModule;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Component;
import rx.Observable;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventsActivityTest {

    @Inject
    IJsonFileAccessor fileAccessor;
    @Inject
    EventRepo eventRepo;

    @Singleton
    @Component(modules = {ClockModule.class, MockEventRepoModule.class})
    public interface TestComponent extends YearlyAppComponent{
        void inject(EventsActivityTest eventsActivityTest);
    }


    @Rule
    public ActivityTestRule<EventsActivity> activityTestRule = new ActivityTestRule<EventsActivity>(EventsActivity.class,
            true,  //initialTouchMode
            false); //launchActivity. False we need to set the mock file accessor

    @Before
    public void setUp() throws IOException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp)instrumentation.getTargetContext().getApplicationContext();
        TestComponent component = DaggerEventsActivityTest_TestComponent.builder()
                .mockEventRepoModule(new MockEventRepoModule())
                .build();
        app.setComponent(component);
        component.inject(this);
        activityTestRule.launchActivity(new Intent());
        when(fileAccessor.read()).thenReturn(new JsonObject());
    }

    @Test
    public void testOneEventInList()  {
        eventRepo.add(new Birthday("John", Date.APRIL, 23, new Clock(), new UUID()));
        onView(withText(containsString("John"))).check(matches(isDisplayed()));
    }

    @Test
    public void testOneEventRemoveLast() throws IOException {
        eventRepo.add(new Birthday("One", Date.APRIL, 23, new Clock(), new UUID()));
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("One")))));
        onView(withText(containsString("One"))).perform(swipeLeft());
        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("One"))))));
    }

    @Test
    public void testTwoEventsRemoveLast() throws IOException {
        eventRepo.add(new Birthday("One", Date.APRIL, 23, new Clock(), new UUID()));
        eventRepo.add(new Birthday("Two", Date.APRIL, 24, new Clock(), new UUID()));
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Two")))));
        onView(withText(containsString("Two"))).perform(swipeLeft());
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("One")))));
        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("Two"))))));
    }
    @Test
    public void testPushAddBirthdayStartsNewActivity() throws IOException {
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_birthday)).perform(click());
        onView(withText(R.string.title_activity_add_birthday)).check(matches(isDisplayed()));
    }

}
