package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.DateFormatter;
import com.lweynant.yearly.controller.list_events.EventsAdapter;
import com.lweynant.yearly.controller.list_events.ListEventsActivity;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.matcher.ToolBarTitleMatcher;
import com.lweynant.yearly.model.Birthday;
import com.lweynant.yearly.model.Date;
import com.lweynant.yearly.model.Event;
import com.lweynant.yearly.model.IEvent;
import com.lweynant.yearly.model.IEventRepo;
import com.lweynant.yearly.model.ITransaction;
import com.lweynant.yearly.model.ModelModule;
import com.lweynant.yearly.model.NotificationTime;
import com.lweynant.yearly.platform.IAlarm;
import com.lweynant.yearly.platform.IClock;
import com.lweynant.yearly.platform.IEventNotification;
import com.lweynant.yearly.platform.IJsonFileAccessor;
import com.lweynant.yearly.platform.IUniqueIdGenerator;
import com.lweynant.yearly.ui.ViewModule;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
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
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.Espresso.registerIdlingResources;
import static android.support.test.espresso.Espresso.unregisterIdlingResources;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.lweynant.yearly.action.OrientationChangeAction.orientationLandscape;
import static com.lweynant.yearly.matcher.RecyclerViewMatcher.withRecyclerView;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class EventsActivityTest {



    @PerApp
    @Component(dependencies = TestPlatformComponent.class, modules = {YearlyAppModule.class, TestSyncControllerModule.class, ViewModule.class, ModelModule.class, ControllerModule.class})
    public interface TestComponentBase extends BaseYearlyAppComponent {
        void inject(EventsActivityTest eventsActivityTest);
    }

    @Inject IJsonFileAccessor fileAccessor;
    @Inject ITransaction transaction;
    @Inject IClock clock;
    @Inject IUniqueIdGenerator idGenerator;
    @Inject EventsAdapter eventsAdapter;
    @Inject CountingIdlingResource idlingResource;
    @Inject DateFormatter dateFormatter;
    @Inject IAlarm alarm;
    @Inject IEventNotification eventNotification;
    @Inject IEventRepo repo;
    @Inject ListEventsContract.UserActionsListener presenter;

    private LocalDate today;
    private LocalDate tomorrow;

    @Rule public ActivityTestRule<ListEventsActivity> activityTestRule = new ActivityTestRule<ListEventsActivity>(ListEventsActivity.class,
            true,  //initialTouchMode
            false); //launchActivity. False we need to set the mock file accessor

    @Before public void setUp() throws IOException {
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        YearlyApp app = (YearlyApp) instrumentation.getTargetContext().getApplicationContext();

        TestPlatformComponent platformComponent = DaggerTestPlatformComponent.builder()
                .mockPlatformModule(new MockPlatformModule()).build();
        TestComponentBase component = DaggerEventsActivityTest_TestComponentBase.builder()
                .testPlatformComponent(platformComponent)
                .yearlyAppModule(new YearlyAppModule(app))
                .viewModule(new ViewModule())
                .modelModule(new ModelModule())
                .controllerModule(new ControllerModule(app))
                .build();
        component.inject(this);
        Timber.d("injected component, file accessor %s", fileAccessor.toString());
        when(fileAccessor.read()).thenReturn(new JsonObject());
        today = new LocalDate(2015, Date.JANUARY, 1);
        tomorrow =today.plusDays(1);
        when(clock.now()).thenReturn(today);
        when(clock.timestamp()).thenReturn("fake timestamp");
        app.setComponent(component);
        registerIdlingResources(idlingResource);

    }


    @After public void tearDown() {
        unregisterIdlingResources(idlingResource);
    }



    @Test public void testOneEventInListWithTodaysBirthday() {
        initializeTheListWith(createBirthday("John", today));
        activityTestRule.launchActivity(new Intent());

        onView(withText(containsString("John"))).check(matches(isDisplayed()));
        Timber.d("verify alarm %s", alarm);
        verify(alarm, times(1)).scheduleAlarm(today, NotificationTime.MORNING);
    }
    @Test public void testOneEventInListWithBirthdayInFuture() {
        LocalDate birthday = today.plusDays(100);
        IEvent event = createBirthday("John", birthday);
        initializeTheListWith(event);
        activityTestRule.launchActivity(new Intent());

        onView(withText(containsString("John"))).check(matches(isDisplayed()));
        verify(alarm, times(1)).scheduleAlarm(birthday.minusDays(event.getNbrOfDaysForNotification()), NotificationTime.EVENING);
    }


    @Test public void testOrderInNonEmptyList() {
        initializeTheListWith(createBirthday("Yesterday", today.minusDays(1)),
                createBirthday("Tomorrow", today.plusDays(1)),
                createBirthday("Today", today));
        activityTestRule.launchActivity(new Intent());

        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.birthday_list_item_name)).check(matches(withText(containsString("Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(2, R.id.birthday_list_item_name)).check(matches(withText(containsString("Yesterday"))));
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
        verifyNoMoreInteractions(alarm);
    }
    @Test public void testListRemainsAfterOrientationChange() {
        initializeTheListWith(createBirthday("Yesterday", today.minusDays(1)),
                              createBirthday("Tomorrow", today.plusDays(1)),
                              createBirthday("Today", today));
        activityTestRule.launchActivity(new Intent());

        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.birthday_list_item_name)).check(matches(withText(containsString("Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(2, R.id.birthday_list_item_name)).check(matches(withText(containsString("Yesterday"))));
        onView(isRoot()).perform(orientationLandscape());
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.birthday_list_item_name)).check(matches(withText(containsString("Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(2, R.id.birthday_list_item_name)).check(matches(withText(containsString("Yesterday"))));
    }


    @Test public void testOneEventRemoveLast() throws IOException {
        IEvent onesBirthday = createBirthday("One");
        initializeTheListWith(onesBirthday);
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("One")))));
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("One"))), swipeLeft()));
        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("One"))))));
        verify(alarm, times(1)).clear();
        verify(eventNotification).cancel(onesBirthday.getID());
    }

    @Test public void testTwoEventsRemoveLast() throws IOException {
        initializeTheListWith(createBirthday("One", tomorrow),
                createBirthday("Two", today));
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Two")))));
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Two"))), swipeLeft()));
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("One")))));
        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("Two"))))));
        verify(alarm).scheduleAlarm(today, NotificationTime.EVENING);//we notify One's birthday the day before in the evening
    }

    @Test public void testShowDetails() {
        initializeTheListWith(createBirthday("Joe", today),
                createBirthday("Fred", tomorrow),
                createBirthday("Marie", tomorrow.plusMonths(2)));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));

        ToolBarTitleMatcher.matchToolbarTitle(containsString("Fre"));
    }

    @Test public void testModifyLastName() {
        initializeTheListWith(createBirthday("Joe"),
                createBirthday("Fred"),
                createBirthday("Marie"));
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withId(R.id.edit_text_lastname)).perform(typeText("Flinstone"), closeSoftKeyboard());
        pressBack();
        pressBack();
        //make sure that the last-name is now shown
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withId(R.id.edit_text_lastname)).check(matches(withText("Flinstone")));
        pressBack();
        pressBack();
        //make sure that the last name is not shown on other birthdays
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Marie"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withId(R.id.edit_text_lastname)).check(matches(not(withText("Flinstone"))));

    }
    @Test public void testModifyDateOfBirthday() {
        initializeTheListWith(createBirthday("Today", today),
                createBirthday("Tomorrow", tomorrow));
        activityTestRule.launchActivity(new Intent());

        //make sure that the order is as expected
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.birthday_list_item_name)).check(matches(withText(containsString("Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Tomorrow"))));

        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(Matchers.containsString("Today"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withText(R.string.title_activity_add_birthday)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_first_name)).check(matches(withText("Today")));
        LocalDate future = today.plusDays(5);
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(future.getYear(), future.getMonthOfYear(), future.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(future.getMonthOfYear(), future.getDayOfMonth()))));
        pressBack();
        pressBack();
        //now the order should be reversed
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.birthday_list_item_name)).check(matches(withText(containsString("Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Today"))));
    }
    @Test public void testModifyDateOfEvent() {
        initializeTheListWith(createEvent("Today", today),
                createEvent("Tomorrow", tomorrow));
        activityTestRule.launchActivity(new Intent());

        //make sure that the order is as expected
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.event_list_item_name)).check(matches(withText(containsString("Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.event_list_item_name)).check(matches(withText(containsString("Tomorrow"))));

        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Today"))), click()));
        onView(withText(R.string.title_activity_add_event)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_event_name)).check(matches(withText("Today")));
        LocalDate future = today.plusDays(5);
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(future.getYear(), future.getMonthOfYear(), future.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_event_date)).check(matches(withText(dateFormatter.format(future.getMonthOfYear(), future.getDayOfMonth()))));
        pressBack();
        //now the order should be reversed
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.event_list_item_name)).check(matches(withText(containsString("Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.event_list_item_name)).check(matches(withText(containsString("Today"))));
    }

    private IEvent createEvent(String name, LocalDate date) {
        //noinspection ResourceType
        return new Event(name, date.getMonthOfYear(), date.getDayOfMonth(), clock, idGenerator);
    }

    @Test public void testPushAddBirthdayStartsNewActivity() throws IOException {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_birthday)).perform(click());
        onView(withText(R.string.title_activity_add_birthday)).check(matches(isDisplayed()));
    }

    @Test public void testAddBirthdayOnEmptyList() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        pressBack();

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Joe")))));
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
    }
    public void testAddBirthdayOnEmptyListPressHome() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        //openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getTargetContext());
        onView(withId(android.R.id.home)).perform(click());

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Joe")))));
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
    }
    @Test public void testAddEventOnEmptyList() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_event)).perform(click());
        enterEvent("Marriage", today);
        pressBack();

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Marriage")))));
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
    }

    @Test public void testAddBirthDayOnNonEmptyList() {
        initializeTheListWith(createBirthday("Yesterday", today.minusDays(1)),
                createBirthday("Today", today));
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.fab_expand_menu_button)).perform(click());
        onView(withId(R.id.action_add_birthday)).perform(click());
        enterBirthday("Tomorrow", today.plusDays(1));
        pressBack();

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Tomorrow")))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(0, R.id.birthday_list_item_name)).check(matches(withText(containsString("Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(2, R.id.birthday_list_item_name)).check(matches(withText(containsString("Yesterday"))));
        verify(alarm, atLeastOnce()).scheduleAlarm(today, NotificationTime.MORNING);
    }

    private void initializeTheListWith(IEvent ... birthdays) {
        for (IEvent birthday: birthdays) {
            transaction.add(birthday);
        }
        transaction.commit();
        //presenter.loadEvents(true);
    }

    private IEvent createBirthday(String name) {
        return new Birthday(name, Date.DECEMBER, 20, clock, idGenerator);
    }

    private IEvent createBirthday(String name, LocalDate date) {
        //noinspection ResourceType
        return new Birthday(name, date.getYear(), date.getMonthOfYear(), date.getDayOfMonth(), clock, idGenerator);
    }

    private void enterBirthday(String firstName, LocalDate date) {
        onView(withId(R.id.edit_text_first_name)).perform(typeText(firstName), closeSoftKeyboard());
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(date.getMonthOfYear(), date.getDayOfMonth()))));
    }
    private void enterEvent(String eventName, LocalDate date) {
        onView(withId(R.id.edit_text_event_name)).perform(typeText(eventName), closeSoftKeyboard());
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(date.getYear(), date.getMonthOfYear(), date.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_event_date)).check(matches(withText(dateFormatter.format(date.getMonthOfYear(), date.getDayOfMonth()))));
    }


}
