package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import android.os.SystemClock;

import androidx.test.espresso.assertion.ViewAssertions;

import org.junit.Rule;
import org.junit.Test;

public class OrganizerMainActivityTest {

    /**
     * Rule to start the AttendeeMainActivity before each test method.
     * This ensures a fresh instance of the activity is used for testing.
     */
    @Rule
    public ActivityScenarioRule<OrgMainActivity> activityScenarioRule = new ActivityScenarioRule<>(OrgMainActivity.class);

    /**
     * Tests whether the AttendeeProfileActivity is started upon clicking the profile button.
     * This test simulates a user action of clicking the profile button and verifies
     * that the AttendeeProfileActivity is correctly launched.
     */
    @Test
    public void testCreateEventNavigation() {
        // Simulate a click on the create event button
        onView(ViewMatchers.withId(R.id.create_event_button)).perform(click());

        // Check if the create event page is open
        onView(ViewMatchers.withId(R.id.event_location_input)).check(matches(isDisplayed()));
    }

    /**
     * Tests that the select account type button  returns to the select page
     */
    @Test
    public void testSelectAccount() {
        // Simulate a click on the switch account
        onView(ViewMatchers.withId(R.id.org_profile_button)).perform(click());
        SystemClock.sleep(2000);
        // Check if we went back to the select role page
        onView(withId(R.id.choose_role)).check(ViewAssertions.matches(isDisplayed()));
    }

}
