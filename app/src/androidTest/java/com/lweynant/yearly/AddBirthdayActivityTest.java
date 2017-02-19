package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lweynant.yearly.controller.add_event.AddBirthdayActivity;
import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.ModelModule;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.ui.ViewModule;

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
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.lweynant.yearly.action.OrientationChangeAction.orientationLandscape;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class AddBirthdayActivityTest {

    private LocalDate today;

    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class,
            SyncControllerModule.class, ViewModule.class, ModelModule.class, ControllerModule.class})
    public interface TestComponent extends BaseYearlyAppComponent {
        void inject(AddBirthdayActivityTest addBirthdayActivityTest);
    }
    @Rule
    public ActivityTestRule<AddBirthdayActivity> activityTestRule = new ActivityTestRule<AddBirthdayActivity>(AddBirthdayActivity.class,
            true,  //initialTouchMode
            false); //launchActivity. False we need to set the mock file accessor
    @Inject IClock clock;
    @Inject IDateFormatter dateFormatter;

    @Before
    public void setUp() throws IOException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        TestComponent component = DaggerAddBirthdayActivityTest_TestComponent.builder()
                .testPlatformComponent(platformComponent)
                .yearlyAppModule(new YearlyAppModule(app))
                .viewModule(new ViewModule())
                .modelModule(new ModelModule())
                .controllerModule(new ControllerModule(app))
                .build();
        app.setComponent(component);
        component.inject(this);
        today = new LocalDate(2015, Date.JANUARY, 1);
        when(clock.now()).thenReturn(today);
        activityTestRule.launchActivity(new Intent());
    }

    @Test public void selectCurrentDay() {
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(Date.JANUARY, 1))));
    }

    @Test public void selectCurrentDayWithYear() {
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.checkbox_add_year)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(2015, Date.JANUARY, 1))));
    }
    @Test public void selectADay() {
        onView(withId(R.id.edit_text_birthday_date)).perform(scrollTo(), click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(Date.FEBRUARY, 8))));
        onView(withId(R.id.action_save)).check(matches(not(isEnabled())));
    }
    @Test public void selectADayWithYear() {
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));
        onView(withId(R.id.checkbox_add_year)).perform(click());

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(1966, Date.FEBRUARY, 8))));
    }

    @Test public void saveIsEnabledWhenFirstNameAndDateIsGiven(){
        onView(withId(R.id.edit_text_first_name)).perform(scrollTo(), typeText("Joe"));
        addDate(today);
        onView(withId(R.id.action_save)).check(matches(isEnabled()));
    }


    @Test public void saveIsNotEnabledWhenOnlyFirstNameIsGiven(){
        onView(withId(R.id.edit_text_first_name)).perform(typeText("Joe"));

        onView(withId(R.id.action_save)).check(matches(not(isEnabled())));
    }
    @Test public void saveIsNotEnabledWhenOnlyDateIsGiven(){
        addDate(today);

        onView(withId(R.id.action_save)).check(matches(not(isEnabled())));
    }
    @Test public void saveIsNotEnabledAtStart(){

        onView(withId(R.id.action_save)).check(matches(not(isEnabled())));
    }


    private void addDate(LocalDate date) {
        onView(withId(R.id.edit_text_birthday_date)).perform(scrollTo(),click());
        onView(withId(R.id.date_picker)).perform(setDate(date.getYear(),date.getMonthOfYear(), date.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
    }

    @Test public void configChangeSavesName() {
        onView(withId(R.id.edit_text_first_name)).perform(typeText("John"), closeSoftKeyboard());
        onView(isRoot()).perform(orientationLandscape());
        onView(withId(R.id.edit_text_first_name)).perform(scrollTo());
        onView(withId(R.id.edit_text_first_name)).check(matches(withText("John")));
    }
    @Test public void configChangeSavesDate() throws InterruptedException {
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withText(R.string.apply)).perform(click());
        onView(isRoot()).perform(orientationLandscape());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_birthday_date))
                .check(matches(withText(dateFormatter.format(today.getMonthOfYear(), today.getDayOfMonth()))));
    }

    @Test public void selectADayAfterBeforeAndAfterConfig() {
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(1966, Date.FEBRUARY, 8));

        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(Date.FEBRUARY, 8))));
        onView(isRoot()).perform(orientationLandscape());
        onView(withId(R.id.edit_text_birthday_date)).perform(scrollTo(), click());
        onView(withText(R.string.apply)).perform(click());
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(Date.FEBRUARY, 8))));
    }


}
