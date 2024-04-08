package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import android.Manifest;
import android.os.SystemClock;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OrganizerIntentTest extends BaseIntentTest{

    /**
     * Rule to start the AttendeeMainActivity before each test method.
     * This ensures a fresh instance of the activity is used for testing.
     */
    @Rule
    public ActivityScenarioRule<LandingPage> activityScenarioRule = new ActivityScenarioRule<>(LandingPage.class);

    @Rule
    public GrantPermissionRule notificationRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    /**
     * Tests the flow of creating a profile and then an event
     */
    @Test
    public void testEventIntent() {
        onView(withText("Create Profile")).perform(click());
        enterProfileDetails("Test User",  "7805555555", "eventlinkqr.com");
        SystemClock.sleep(3000);

        enterEventDetails("Intent Test Event", "Intent testing event", "123 456 Street, Cityville, Ohio");

        performActionWithRetryOnText("My Events", click(), 5, 1000);

        performCheckWithRetryOnText("Intent Test Event", isDisplayed(), 5, 1000);

        performActionWithRetryOnText("Intent Test Event", click(), 5, 1000);

        performCheckWithRetry(R.id.org_event_location, withText("123 456 Street, Cityville, Ohio"), 5, 1000);
        performCheckWithRetry(R.id.org_event_description, withText("Intent testing event"), 5, 1000);
        performCheckWithRetry(R.id.org_event_category, withText("Workshop"), 5, 1000);

        performActionWithRetry(R.id.edit_event_button, click(), 5, 2000);
        SystemClock.sleep(2000);
        onView(withId(R.id.event_name_input)).perform(replaceText("Changed Event Name"), ViewActions.closeSoftKeyboard());
        performActionWithRetry(R.id.publish_button, click(), 5, 2000);

        performActionWithRetryOnText("My Events", click(), 5, 1000);
        performCheckWithRetryOnText("Changed Event Name", isDisplayed(), 5, 1000);

    }
}
