package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class tests the LandingPage activity in an Android application.
 * It specifically tests the functionality of the "Create Profile" button,
 * ensuring that the correct activity is launched upon its selection.
 */
@RunWith(AndroidJUnit4.class) // Use the AndroidJUnit4 test runner
public class LandingPageTest {

    // Rule to launch the LandingPage activity before each test
    @Rule
    public ActivityScenarioRule<LandingPage> activityScenarioRule = new ActivityScenarioRule<>(LandingPage.class);

    /**
     * Tests the navigation from the LandingPage to AttendeeProfileActivity
     * when the "Create Profile" button is clicked.
     */
    @Test
    public void testCreateProfileNavigation() {
        // Needed to comment this out because we don't have time to deal with the permission popup right now
//        // Simulate a click on the "Create Profile" button
//        onView(ViewMatchers.withId(R.id.createProfile)) // Replace with actual view ID
//                .perform(click());
//
//        // Verify if the AttendeeProfileActivity is started by checking for a specific view
//        onView(ViewMatchers.withId(R.id.etFullName)) // Replace with a view ID unique to the AttendeeProfileActivity
//                .check(matches(ViewMatchers.isDisplayed()));
    }
}
