package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class contains tests for the AttendeeMainActivity of an Android application.
 * It verifies that the navigation within the activity works as expected.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AttendeeMainActivityTest {

    /**
     * Rule to start the AttendeeMainActivity before each test method.
     * This ensures a fresh instance of the activity is used for testing.
     */
    @Rule
    public ActivityScenarioRule<AttendeeMainActivity> activityScenarioRule = new ActivityScenarioRule<>(AttendeeMainActivity.class);

    /**
     * Tests whether the AttendeeProfileActivity is started upon clicking the profile button.
     * This test simulates a user action of clicking the profile button and verifies
     * that the AttendeeProfileActivity is correctly launched.
     */
    @Test
    public void testProfileButtonNavigation() {
        // Simulate a click on the profile button and verify the navigation
        onView(ViewMatchers.withId(R.id.attendee_profile_button)) // Replace with actual button ID in your app
                .perform(click());

        // Check if the AttendeeProfileActivity is started by looking for a unique view in that activity
        onView(ViewMatchers.withId(R.id.etFullName)) // Replace with a view ID unique to the AttendeeProfileActivity
                .check(matches(ViewMatchers.isDisplayed()));
    }
    
    @Test
    public void openCamera() {
        try (ActivityScenario<AttendeeMainActivity> activityScenario = ActivityScenario.launch(AttendeeMainActivity.class)) {
            onView(withId(R.id.attendee_scan_button)).perform(click());

            // Need to evaluate if this test is possible (it involves the Google QR Code scanner)
        }
    }
}
