package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.controller.add_event.AddEventActivity;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.ModelModule;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.ui.ViewModule;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.Component;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.lweynant.yearly.action.OrientationChangeAction.orientationLandscape;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AddEventActivityTest {
    private org.joda.time.LocalDate today;

    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class,
            SyncControllerModule.class, ViewModule.class, ModelModule.class, ControllerModule.class})
    public interface TestComponent extends BaseYearlyAppComponent {
        void inject(AddEventActivityTest addEventActivityTest);
    }

    @Inject IStringResources rstring;
    @Inject IClock clock;
    @Inject DateFormatter dateFormatter;

    @Rule public ActivityTestRule<AddEventActivity> activityTestRule = new ActivityTestRule<AddEventActivity>(AddEventActivity.class,
            true, //initial touch mode
            false); //launchActivity false, we need to inject dependencies

    @Before public void setUp() {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        TestComponent component = DaggerAddEventActivityTest_TestComponent.builder()
                .testPlatformComponent(platformComponent)
                .yearlyAppModule(new YearlyAppModule(app))
                .viewModule(new ViewModule())
                .modelModule(new ModelModule())
                .controllerModule(new ControllerModule(app))
                .build();
        app.setComponent(component);
        component.inject(this);
        today = new LocalDate(2016, Date.JANUARY, 30);
        when(clock.now()).thenReturn(today);

        activityTestRule.launchActivity(new Intent());
    }

    @Test public void initialUIIsDisplayed()  {
        onView(withId(R.id.edit_text_event_name)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_event_date)).check(matches(isDisplayed()));
    }

    @Test public void configChangeSavesName() {
        onView(withId(R.id.edit_text_event_name)).perform(typeText("Important event"), closeSoftKeyboard());
        onView(isRoot()).perform(orientationLandscape());
        onView(withId(R.id.edit_text_event_name)).check(matches(withText("Important event")));
    }
    @Test public void configChangeSavesDate() {
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        onView(isRoot()).perform(orientationLandscape());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_event_date))
                .check(matches(withText(dateFormatter.format(today.getMonthOfYear(), today.getDayOfMonth()))));
    }
    @Test public void selectCurrentDay() {
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_event_date))
                .check(matches(withText(dateFormatter.format(today.getMonthOfYear(), today.getDayOfMonth()))));
    }
    @Test public void selectCurrentDayWithYear() {
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withId(R.id.checkbox_add_year)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_event_date))
                .check(matches(withText(dateFormatter.format(today.getYear(), today.getMonthOfYear(), today.getDayOfMonth()))));
    }
    @Test public void selectADay() {
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_event_date)).check(matches(withText(dateFormatter.format(Date.FEBRUARY, 8))));
    }
    @Test public void selectADayWithYear() {
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));
        onView(withId(R.id.checkbox_add_year)).perform(click());

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_event_date)).check(matches(withText(dateFormatter.format(1966, Date.FEBRUARY, 8))));
    }




}
