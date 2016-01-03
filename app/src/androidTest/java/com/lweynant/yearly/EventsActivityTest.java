package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.JsonObject;
import com.lweynant.yearly.controller.EventControllerModule;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.controller.EventsAdapter;
import com.lweynant.yearly.controller.EventsAdapterModule;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventModelModule;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.ui.EventViewModule;
import com.lweynant.yearly.util.IClock;
import com.lweynant.yearly.util.IUniqueIdGenerator;
import com.lweynant.yearly.util.UUID;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Component;
import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventsActivityTest {


    @Before
    public void setUp() throws IOException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        TestComponentBase component = DaggerEventsActivityTest_TestComponentBase.builder()
                .testPlatformComponent(platformComponent)
                .yearlyAppModule(new YearlyAppModule(app))
                .eventViewModule(new EventViewModule())
                .eventModelModule(new EventModelModule())
                .build();
        app.setComponent(component);
        component.inject(this);
        registerIdlingResources(idlingResource);
        Timber.d("injected component, file accessor %s", fileAccessor.toString());
        when(fileAccessor.read()).thenReturn(new JsonObject());
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JANUARY, 1));
        when(clock.timestamp()).thenReturn("fake timestamp");
        activityTestRule.launchActivity(new Intent());
    }


    @After
    public void tearDown() {
        unregisterIdlingResources(idlingResource);
    }

    @Rule
    public ActivityTestRule<EventsActivity> activityTestRule = new ActivityTestRule<EventsActivity>(EventsActivity.class,
            true,  //initialTouchMode
            false); //launchActivity. False we need to set the mock file accessor
    @Inject IJsonFileAccessor fileAccessor;
    @Inject EventRepo eventRepo;
    @Inject IClock clock;
    @Inject IUniqueIdGenerator idGenerator;
    @Inject EventsAdapter eventsAdapter;
    @Inject CountingIdlingResource idlingResource;

    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class, TestEventsAdapterModule.class, EventViewModule.class, EventModelModule.class, EventControllerModule.class})
    public interface TestComponentBase extends BaseYearlyAppComponent {
        void inject(EventsActivityTest eventsActivityTest);
    }

    @Test
    public void testOneEventInList() {
        eventRepo.add(new Birthday("John", Date.APRIL, 23, clock, idGenerator))
                 .commit();
        onView(withText(containsString("John"))).check(matches(isDisplayed()));
    }

    @Test
    public void testOneEventRemoveLast() throws IOException {
        eventRepo.add(new Birthday("One", Date.APRIL, 23, clock, idGenerator))
                 .commit();
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("One")))));
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem((withText(containsString("One"))), swipeLeft()));
        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("One"))))));
    }

    @Test
    public void testTwoEventsRemoveLast() throws IOException {
        eventRepo.add(new Birthday("One", Date.APRIL, 23, clock, idGenerator))
                 .add(new Birthday("Two", Date.APRIL, 24, clock, idGenerator))
                 .commit();
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Two")))));
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem((withText(containsString("Two"))), swipeLeft()));
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
