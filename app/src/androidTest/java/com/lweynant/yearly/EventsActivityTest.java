package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.JsonObject;
import com.lweynant.yearly.controller.EventsActivity;
import com.lweynant.yearly.model.EventRepo;
import com.lweynant.yearly.model.EventRepoFileAccessor;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IJsonFileAccessor;
import com.lweynant.yearly.util.ClockModule;

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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventsActivityTest {

    @Inject
    IJsonFileAccessor fileAccessor;
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
    public void setUp(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp)instrumentation.getTargetContext().getApplicationContext();
        TestComponent component = DaggerEventsActivityTest_TestComponent.builder()
                .mockEventRepoModule(new MockEventRepoModule())
                .build();
        app.setComponent(component);
        component.inject(this);
        activityTestRule.launchActivity(new Intent());
    }

    @Test
    public void testPushAddBirthdayStartsNewActivity() throws IOException {
        when(fileAccessor.read()).thenReturn(new JsonObject());
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_birthday)).perform(click());
        onView(withText(R.string.title_activity_add_birthday)).check(matches(isDisplayed()));
    }

}
