package com.lweynant.yearly;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.google.gson.JsonObject;
import com.lweynant.yearly.controller.ControllerModule;
import com.lweynant.yearly.controller.list_events.EventsAdapter;
import com.lweynant.yearly.controller.list_events.ListBirthdaysActivity;
import com.lweynant.yearly.controller.list_events.ListEventsActivity;
import com.lweynant.yearly.controller.list_events.ListEventsContract;
import com.lweynant.yearly.matcher.CollapsingToolBarTitleMatcher;
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
import com.lweynant.yearly.platform.IRawAlarm;
import com.lweynant.yearly.platform.IUniqueIdGenerator;
import com.lweynant.yearly.ui.ViewModule;

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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.PickerActions.setDate;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withChild;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.lweynant.yearly.action.OrientationChangeAction.orientationLandscape;
import static com.lweynant.yearly.matcher.RecyclerViewMatcher.withRecyclerView;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
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
    @Inject IDateFormatter dateFormatter;
    @Inject IRawAlarm alarm;
    @Inject IEventNotification eventNotification;
    @Inject IEventRepo repo;
    @Inject ListEventsContract.UserActionsListener presenter;

    private LocalDate today;
    private LocalDate tomorrow;

    @Rule public ActivityTestRule<ListBirthdaysActivity> activityTestRule = new ActivityTestRule<ListBirthdaysActivity>(ListBirthdaysActivity.class,
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
        today = new LocalDate(2015, Date.JANUARY, 10);
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
    @Test public void testTwoEventsInListWithTodayAndTomorrowBirthday_AtStartOfDay() {
        when(clock.hour()).thenReturn(NotificationTime.START_OF_DAY);
        initializeTheListWith(createBirthday("John", today),
                createBirthday("Fred", tomorrow));
        activityTestRule.launchActivity(new Intent());

        onView(withText(containsString("John"))).check(matches(isDisplayed()));
        verify(alarm, times(1)).scheduleAlarm(today, NotificationTime.MORNING);
        verifyNoMoreInteractions(alarm);
    }
    
    @Test public void testTwoEventsInListWithTodayAndTomorrowBirthday_InMorning() {
        when(clock.hour()).thenReturn(NotificationTime.MORNING);
        initializeTheListWith(createBirthday("John", today),
                createBirthday("Fred", tomorrow));
        activityTestRule.launchActivity(new Intent());

        onView(withText(containsString("John"))).check(matches(isDisplayed()));
        verify(alarm, times(1)).scheduleAlarm(today, NotificationTime.EVENING);
        verifyNoMoreInteractions(alarm);
    }
    @Test public void testTwoEventsInListWithTodayAndTomorrowBirthday_AtEvening() {
        when(clock.hour()).thenReturn(NotificationTime.EVENING);
        initializeTheListWith(createBirthday("John", today),
                createBirthday("Fred", tomorrow));
        activityTestRule.launchActivity(new Intent());

        onView(withText(containsString("John"))).check(matches(isDisplayed()));
        verify(alarm, times(1)).scheduleAlarm(tomorrow, NotificationTime.MORNING);
        verifyNoMoreInteractions(alarm);
    }
    @Test public void testOneEventInListWithBirthdayInFuture() {
        LocalDate birthday = today.plusDays(100);
        IEvent event = createBirthday("John", birthday);
        initializeTheListWith(event);
        activityTestRule.launchActivity(new Intent());

        onView(withText(containsString("John"))).check(matches(isDisplayed()));
        verify(alarm, times(1)).scheduleAlarm(birthday.minusDays(event.getNbrOfDaysForNotification()), NotificationTime.EVENING);
    }

    @Test public void testGitHubIssue35() throws InterruptedException {
        initializeTheListWith(createBirthday("Mr Yesterday", today.minusDays(1)),
                createBirthday("Mr This Month", today.plusDays(5)),
                createBirthday("Mr Next Month", today.plusMonths(1)));
        activityTestRule.launchActivity(new Intent());

        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr This Month"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Next Month"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(5, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Yesterday"))));
    }

    @Test public void testOrderInNonEmptyList() {
        initializeTheListWith(createBirthday("Mr Yesterday", today.minusDays(1)),
                createBirthday("Mr Tomorrow", today.plusDays(1)),
                createBirthday("Mr Today", today));
        activityTestRule.launchActivity(new Intent());


        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(5, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Yesterday"))));
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
        verifyNoMoreInteractions(alarm);
    }
    @Test public void testListRemainsAfterOrientationChange() {
        initializeTheListWith(createBirthday("Mr Yesterday", today.minusDays(1)),
                              createBirthday("Mr Tomorrow", today.plusDays(1)),
                              createBirthday("Mr Today", today));
        activityTestRule.launchActivity(new Intent());

        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(5, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Yesterday"))));
        onView(isRoot()).perform(orientationLandscape());
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));
        //too many views the last one is not visible, so the assert will fail
        //onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(5, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Yesterday"))));
    }




    @Test public void testShowDetails() {
        initializeTheListWith(createBirthday("Joe", today),
                createBirthday("Fred", tomorrow),
                createBirthday("Marie", tomorrow.plusMonths(2)));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));

        CollapsingToolBarTitleMatcher.matchToolbarTitle(containsString("Fred"));
    }
    @Test public void testShowDetailsPressUp() {
        initializeTheListWith(createBirthday("Joe", today),
                createBirthday("Fred", tomorrow),
                createBirthday("Marie", tomorrow.plusMonths(2)));
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));

        CollapsingToolBarTitleMatcher.matchToolbarTitle(containsString("Fred"));
        pressNavigateUp();
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name))
                .check(matches(withText(containsString("Joe"))));

    }

    @Test public void testModifyLastName() {
        initializeTheListWith(createBirthday("Joe"),
                createBirthday("Fred"),
                createBirthday("Marie"));
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withId(R.id.edit_text_lastname)).perform(typeText("Flinstone"), closeSoftKeyboard());

        onView(withText(R.string.action_save)).perform(click());
        pressBack();

        //make sure that the last-name is now shown
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Fred"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withId(R.id.edit_text_lastname)).check(matches(withText("Flinstone")));
        //because of know bug (save button is enabled on add we have to press save instead of pressBack
        //pressBack();
        onView(withText(R.string.action_save)).perform(click());
        pressBack();
        //make sure that the last name is not shown on other birthdays
        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Marie"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withId(R.id.edit_text_lastname)).check(matches(not(withText("Flinstone"))));

    }
    @Test public void testModifyDateOfBirthday() {
        initializeTheListWith(createBirthday("Mr Today", today),
                createBirthday("Mr Tomorrow", tomorrow));
        activityTestRule.launchActivity(new Intent());

        //make sure that the order is as expected
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));

        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(Matchers.containsString("Mr Today"))), click()));
        onView(withId(R.id.fab_edit_birthday)).perform(click());
        onView(withText(R.string.title_activity_add_birthday)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_first_name)).check(matches(withText("Mr Today")));
        LocalDate future = today.plusDays(5);
        onView(withId(R.id.edit_text_birthday_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(future.getYear(), future.getMonthOfYear(), future.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_birthday_date)).check(matches(withText(dateFormatter.format(future.getMonthOfYear(), future.getDayOfMonth()))));
        onView(withText(R.string.action_save)).perform(click());
        pressBack();
        //now the order should be reversed
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
    }
    @Test public void testModifyDateOfEvent() {
        initializeTheListWith(createEvent("Mr Today", today),
                createEvent("Mr Tomorrow", tomorrow));
        activityTestRule.launchActivity(new Intent());

        //make sure that the order is as expected
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.event_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.event_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));

        onView(withId(R.id.events_recycler_view)).perform(RecyclerViewActions.actionOnItem(withChild(withText(containsString("Mr Today"))), click()));
        onView(withText(R.string.title_activity_add_event)).check(matches(isDisplayed()));
        onView(withId(R.id.edit_text_event_name)).check(matches(withText("Mr Today")));
        LocalDate future = today.plusDays(5);
        onView(withId(R.id.edit_text_event_date)).perform(click());
        onView(withId(R.id.date_picker)).perform(setDate(future.getYear(), future.getMonthOfYear(), future.getDayOfMonth()));
        onView(withText(R.string.apply)).perform(click());
        //noinspection ResourceType
        onView(withId(R.id.edit_text_event_date)).check(matches(withText(dateFormatter.format(future.getMonthOfYear(), future.getDayOfMonth()))));
        pressBack();
        //now the order should be reversed
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.event_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.event_list_item_name)).check(matches(withText(containsString("Mr Today"))));
    }

    private IEvent createEvent(String name, LocalDate date) {
        //noinspection ResourceType
        return new Event(name, date.getMonthOfYear(), date.getDayOfMonth(), clock, idGenerator);
    }

    @Test public void testPushAddBirthdayStartsNewActivity() throws IOException {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_add_birthday)).perform(click());
        onView(withText(R.string.title_activity_add_birthday)).check(matches(isDisplayed()));
    }
    @Test public void testAddBirthdayOnEmptyList() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        onView(withText(R.string.action_save)).perform(click());
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Joe")))));
        verify(alarm).scheduleAlarm(today, NotificationTime.MORNING);
    }

    @Test public void testAddBirthdayOnEmptyListPressBackAndThrowaway() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        pressBack();
        onView(withText(R.string.add_birthday_throw_away)).perform(click());
        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("Joe"))))));

    }
    @Test public void testAddBirthdayOnEmptyListPressHomeAndThrowAway() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        pressNavigateUp();
        onView(withText(R.string.add_birthday_throw_away)).perform(click());

        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("Joe"))))));
    }

    @Test public void testAddBirthdayOnEmptyListPressBackCancelAndSave() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        pressBack();
        onView(withText(R.string.add_birthday_ask_throw_away_modifications_cancel)).perform(click());
        onView(withText(R.string.action_save)).perform(click());
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Joe")))));
    }

    @Test public void testAddBirthdayOnEmptyListNavigateUpCancelAndSave() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Joe", today);
        pressNavigateUp();
        onView(withText(R.string.add_birthday_ask_throw_away_modifications_cancel)).perform(click());
        onView(withText(R.string.action_save)).perform(click());
        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Joe")))));
    }

    private void sleep() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void pressNavigateUp() {
        onView(withContentDescription(android.support.v7.appcompat.R.string.abc_action_bar_up_description)).perform(click());
    }


    @Test public void testAddBirthDayOnNonEmptyList() {
        initializeTheListWith(createBirthday("Mr Yesterday", today.minusDays(1)),
                createBirthday("Mr Today", today));
        activityTestRule.launchActivity(new Intent());

        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Mr Tomorrow", today.plusDays(1));
        onView(withText(R.string.action_save)).perform(click());

        onView(withId(R.id.events_recycler_view)).check(matches(hasDescendant(withText(containsString("Mr Tomorrow")))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Tomorrow"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(5, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Yesterday"))));
        verify(alarm, atLeastOnce()).scheduleAlarm(today, NotificationTime.MORNING);
    }
    @Test public void testAddBirthDayOnNonEmptyListPressBackAndThrowAway() {
        initializeTheListWith(createBirthday("Mr Yesterday", today.minusDays(1)),
                createBirthday("Mr Today", today));
        activityTestRule.launchActivity(new Intent());
        reset(alarm);
        onView(withId(R.id.fab_add_birthday)).perform(click());
        enterBirthday("Mr Tomorrow", today.plusDays(1));
        pressBack();
        onView(withText(R.string.add_birthday_throw_away)).perform(click());

        onView(withId(R.id.events_recycler_view)).check(matches(not(hasDescendant(withText(containsString("Mr Tomorrow"))))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(1, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Today"))));
        onView(withRecyclerView(R.id.events_recycler_view).atPositionOnView(3, R.id.birthday_list_item_name)).check(matches(withText(containsString("Mr Yesterday"))));
        verifyZeroInteractions(alarm);
    }

    private void pressBackAndSave() {
        pressBack();
        //onView(wi)
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
