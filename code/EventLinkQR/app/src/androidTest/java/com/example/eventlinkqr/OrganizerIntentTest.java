package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import android.Manifest;
import android.os.SystemClock;

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
public class OrganizerIntentTest {

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
    public void testCreateEvent() {
        onView(withText("Create Profile")).perform(click());
        onView(withId(R.id.new_full_name)).perform(typeText("Test User"));
        onView(withId(R.id.new_full_name)).perform(closeSoftKeyboard());
        SystemClock.sleep(3000);
        onView(withText("My Events")).perform(click());
        // Simulate a click on the create event button
        onView(allOf(ViewMatchers.withText("Create event"), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click());

        // Check if the create event page is open
        onView(ViewMatchers.withId(R.id.event_location_input)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.event_name_input)).perform(typeText("Intent Test Event"));
        onView(ViewMatchers.withId(R.id.event_description_input)).perform(typeText("Intent testing event"));
        onView(ViewMatchers.withId(R.id.event_description_input)).perform(closeSoftKeyboard());
        onView(withId(R.id.new_event_geo_switch)).perform(click());
        onView(withId(R.id.category_selector)).perform(click());
        onView(withText("Workshop")).perform(click());
        onView(ViewMatchers.withId(R.id.event_location_input)).perform(typeText("123 456 Street, Cityville, Ohio"));
        onView(ViewMatchers.withId(R.id.date_picker)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("Save and Publish")).perform(click());
        SystemClock.sleep(1000);
        onView(withText("My Events")).perform(click());
        onView(withId(R.id.event_list_view)).check(ViewAssertions.selectedDescendantsMatch(withText("Intent Test Event"), isDisplayed()));
    }
}
