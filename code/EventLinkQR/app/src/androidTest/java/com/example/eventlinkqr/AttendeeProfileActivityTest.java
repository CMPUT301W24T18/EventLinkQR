package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class performs automated UI tests on the AttendeeProfileActivity
 * within an Android application to verify the functionality and navigation.
 */
@RunWith(AndroidJUnit4.class)
public class AttendeeProfileActivityTest {

    /**
     * Rule to launch the AttendeeMainActivity before each test.
     */
    @Rule
    public ActivityScenarioRule<AttendeeMainActivity> activityScenarioRule = new ActivityScenarioRule<>(AttendeeMainActivity.class);

    /**
     * Tests if user profile information is persisted correctly.
     * This method enters and saves user information, then reloads the activity
     * to check if the information is displayed correctly.
     * @throws InterruptedException for handling Thread.sleep in the test
     */
    @Test
    public void testProfileInfoPersistence() throws InterruptedException {
        // Navigate to AttendeeProfileActivity
        onView(withId(R.id.attendee_profile_button)).perform(click());

        // Input the name "Jason" into the EditText and close the keyboard
        onView(withId(R.id.etFullName)).perform(replaceText("Jason"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phoneNumberEdit)).perform(replaceText("1234567891"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.homepageEdit)).perform(replaceText("www.Jason.com"), ViewActions.closeSoftKeyboard());

        // Click on the Save button
        onView(withId(R.id.btnSave)).perform(click());

        // Wait for a bit to let the main activity reload
        Thread.sleep(2000); // Use with caution, just for debugging purposes

        // Navigate back to AttendeeProfileActivity
        onView(withId(R.id.attendee_profile_button)).perform(click());

        // Verify that the name "Jason" is displayed
        onView(withId(R.id.etFullName)).check(matches(withText("Jason")));
        onView(withId(R.id.phoneNumberEdit)).check(matches(withText("1234567891")));
        onView(withId(R.id.homepageEdit)).check(matches(withText("www.Jason.com")));
    }

    /**
     * Tests the back button functionality in the AttendeeProfileActivity.
     * This method navigates to the AttendeeProfileActivity and then uses
     * the back button to check if the navigation works correctly.
     */
    @Test
    public void testBackButtonNavigation() {
        // Navigate to AttendeeProfileActivity
        onView(withId(R.id.attendee_profile_button)).perform(click());

        // Click on the Save button
        onView(withId(R.id.btnBack)).perform(click());

        // Check if the AttendeeProfileActivity is started
        onView(ViewMatchers.withId(R.id.attendee_profile_button)) // Replace with a view ID unique to the AttendeeProfileActivity
                .check(matches(ViewMatchers.isDisplayed()));
    }
}
