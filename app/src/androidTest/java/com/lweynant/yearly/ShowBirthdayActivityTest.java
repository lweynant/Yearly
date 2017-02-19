package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.controller.show_event.ShowBirthdayActivity;
import com.lweynant.yearly.matcher.CollapsingToolBarTitleMatcher;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.ModelModule;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IUniqueIdGenerator;
import com.lweynant.yearly.ui.ViewModule;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;

import dagger.Component;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.lweynant.yearly.action.OrientationChangeAction.orientationLandscape;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ShowBirthdayActivityTest {

    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class,
            SyncControllerModule.class, ViewModule.class, ModelModule.class, ControllerModule.class})
    public interface TestComponent extends BaseYearlyAppComponent {
        void inject(ShowBirthdayActivityTest showBirthdayActivity);

    }

    private LocalDate today;
    @Inject IUniqueIdGenerator idGenerator;
    @Inject IStringResources rstring;
    @Inject IClock clock;
    @Inject IDateFormatter dateFormatter;
    @Inject IEventNotification eventNotification;

    @Rule public ActivityTestRule<ShowBirthdayActivity> activityTestRule = new ActivityTestRule<ShowBirthdayActivity>(ShowBirthdayActivity.class,
            true, //initial touch mode
            false); //launchActivity false, we need to inject dependencies

    @Before public void setUp() {
        Instrumentation instrumentation = getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        TestComponent component = DaggerShowBirthdayActivityTest_TestComponent.builder()
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

    }

    @Test public void showsContentOfBirthday()  {
        Intent startIntent = new Intent();
        setBirthdayOnIntent("Fred", 2000, Date.APRIL, 23, startIntent);
        activityTestRule.launchActivity(startIntent);

        CollapsingToolBarTitleMatcher.matchToolbarTitle(is("Fred"));
        onView(withId(R.id.text_birthday_date)).check(matches(withText(dateFormatter.format(2000, Date.APRIL, 23))));
        onView(withId(R.id.text_birthday_age)).check(matches(withText("15")));
        LocalDate birthDate = new LocalDate(today.getYear(), Date.APRIL, 23);
        onView(withId(R.id.text_birthday_day)).check(matches(withText(birthDate.dayOfWeek().getAsText())));

    }
    @Test public void showsContentOfBirthdayAfterRotationChange()  {
        Intent startIntent = new Intent();
        setBirthdayOnIntent("Fred", 2000, Date.APRIL, 23, startIntent);
        activityTestRule.launchActivity(startIntent);
        onView(isRoot()).perform(orientationLandscape());

        CollapsingToolBarTitleMatcher.matchToolbarTitle(is("Fred"));
        onView(withId(R.id.text_birthday_date)).check(matches(withText(dateFormatter.format(2000, Date.APRIL, 23))));
        onView(withId(R.id.text_birthday_age)).check(matches(withText("15")));
        LocalDate birthDate = new LocalDate(today.getYear(), Date.APRIL, 23);
        onView(withId(R.id.text_birthday_day)).check(matches(withText(birthDate.dayOfWeek().getAsText())));

    }

    @Test public void modifyFirstName () {
        Intent startIntent = new Intent();
        setBirthdayOnIntent("Fred", 2000, Date.APRIL, 23, startIntent);
        activityTestRule.launchActivity(startIntent);

        onView(withId(R.id.fab_edit_birthday)).perform(click());

        onView(withId(R.id.edit_text_first_name)).perform(clearText(), typeText("Uncle Fred"), closeSoftKeyboard());
        onView(withText(R.string.action_save)).perform(click());
        CollapsingToolBarTitleMatcher.matchToolbarTitle(is("Uncle Fred"));

    }
    @Test public void deleteBirthday() {
        Intent startIntent = new Intent();
        Birthday birthday = new Birthday("Joe", 2000, Date.APRIL, 3, clock, idGenerator);
        setBirthdayOnIntent(birthday, startIntent);
        activityTestRule.launchActivity(startIntent);

        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.delete)).perform(click());

        verify(eventNotification).cancel(birthday.getID());
    }


    private void setBirthdayOnIntent(String name, int yearOfBirth, int month, int day, Intent startIntent) {
        Birthday birthday = new Birthday(name, yearOfBirth, month, day, clock, idGenerator);
        setBirthdayOnIntent(birthday, startIntent);
    }

    private void setBirthdayOnIntent(Birthday birthday, Intent startIntent) {
        Bundle bundle = new Bundle();
        birthday.archiveTo(bundle);
        startIntent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
    }

}
