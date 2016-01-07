package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lweynant.yearly.controller.AddBirthdayActivity;
import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.controller.EventControllerModule;
import com.lweynant.yearly.controller.EventsAdapterModule;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.EventModelModule;
import com.lweynant.yearly.ui.EventViewModule;
import com.lweynant.yearly.platform.IClock;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Component;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AddBirthdayActivityTest {

    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class,
            EventsAdapterModule.class, EventViewModule.class, EventModelModule.class, EventControllerModule.class})
    public interface TestComponent extends BaseYearlyAppComponent {
        void inject(AddBirthdayActivityTest addBirthdayActivityTest);
    }
    @Rule
    public ActivityTestRule<AddBirthdayActivity> activityTestRule = new ActivityTestRule<AddBirthdayActivity>(AddBirthdayActivity.class,
            true,  //initialTouchMode
            false); //launchActivity. False we need to set the mock file accessor
    @Inject IClock clock;
    @Inject DateFormatter dateFormatter;

    @Before
    public void setUp() throws IOException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        TestComponent component = DaggerAddBirthdayActivityTest_TestComponent.builder()
                .testPlatformComponent(platformComponent)
                .yearlyAppModule(new YearlyAppModule(app))
                .eventViewModule(new EventViewModule())
                .eventModelModule(new EventModelModule())
                .build();
        app.setComponent(component);
        component.inject(this);
    }

    @Test public void selectCurrentDay() {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JANUARY, 1));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(Date.JANUARY, 1))));
    }

    @Test public void selectCurrentDayWithYear() {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JANUARY, 1));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.checkbox_add_year)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(2015, Date.JANUARY, 1))));
    }
    @Test public void selectADay() {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JANUARY, 1));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(Date.FEBRUARY, 8))));
    }
    @Test public void selectADayWithYear() {
        when(clock.now()).thenReturn(new LocalDate(2015, Date.JANUARY, 1));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));
        onView(withId(R.id.checkbox_add_year)).perform(click());

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(1966, Date.FEBRUARY, 8))));
    }


}
