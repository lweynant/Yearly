package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.SyncControllerModule;
import com.lweynant.yearly.controller.show_event.ShowBirthdayActivity;
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
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.lweynant.yearly.matcher.BirthdayShareIntentMatcher.matchShareIntentWrappedInChooser;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
public class ShowBirthdayActivityIntentTest  {

    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class,
            SyncControllerModule.class, ViewModule.class, ModelModule.class, ControllerModule.class})
    public interface TestComponent extends BaseYearlyAppComponent {
        void inject(ShowBirthdayActivityIntentTest showBirthdayActivity);
    }

    private LocalDate today;
    @Inject IUniqueIdGenerator idGenerator;
    @Inject IStringResources rstring;
    @Inject IClock clock;
    @Inject IDateFormatter dateFormatter;
    @Inject IEventNotification eventNotification;

    @Rule public IntentsTestRule<ShowBirthdayActivity> activityTestRule =
            new IntentsTestRule<ShowBirthdayActivity>(ShowBirthdayActivity.class,
            true, //initial touch mode
            false); //launchActivity false, we need to inject dependencies

    @Before public void setUp() {
        Instrumentation instrumentation = getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        ShowBirthdayActivityIntentTest.TestComponent component = DaggerShowBirthdayActivityIntentTest_TestComponent.builder()
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

    @Test public void testIntentSend(){
        Intent startIntent = new Intent();
        setBirthdayOnIntent("Fred", today.plusDays(1), startIntent);
        activityTestRule.launchActivity(startIntent);

        onView(withId(R.id.menu_item_share)).perform(click());
        intended(matchShareIntentWrappedInChooser(allOf(hasAction(equalTo(Intent.ACTION_SEND)),
                hasExtras(hasEntry(equalTo(Intent.EXTRA_TEXT),
                        allOf(containsString(getTargetContext().getString(R.string.tomorrow)),
                                containsString("Fred")))))));


    }

    @Test public void testIntentSend_NewDay(){
        Intent startIntent = new Intent();
        setBirthdayOnIntent("Fred", today.plusDays(1), startIntent);
        activityTestRule.launchActivity(startIntent);

        when(clock.now()).thenReturn(today.plusDays(1));
        onView(withId(R.id.menu_item_share)).perform(click());

        intended(matchShareIntentWrappedInChooser(allOf(hasAction(equalTo(Intent.ACTION_SEND)),
                hasExtras(hasEntry(equalTo(Intent.EXTRA_TEXT),
                        allOf(containsString(getTargetContext().getString(R.string.today)),
                                containsString("Fred")))))));
    }

    private void setBirthdayOnIntent(String name, LocalDate date, Intent startIntent) {
        //noinspection WrongConstant
        Birthday birthday = new Birthday(name, date.getMonthOfYear(), date.getDayOfMonth(), clock, idGenerator);
        setBirthdayOnIntent(birthday, startIntent);

    }
    private void setBirthdayOnIntent(Birthday birthday, Intent startIntent) {
        Bundle bundle = new Bundle();
        birthday.archiveTo(bundle);
        startIntent.putExtra(IEvent.EXTRA_KEY_EVENT, bundle);
    }

}
