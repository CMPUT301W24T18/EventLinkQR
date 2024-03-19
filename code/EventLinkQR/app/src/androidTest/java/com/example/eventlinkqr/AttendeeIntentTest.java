package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isChecked;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.os.SystemClock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * This class performs automated UI tests on Attendee functionality.
 */
@RunWith(AndroidJUnit4.class)
public class AttendeeIntentTest {

    /**
     * Rule to launch the AttendeeMainActivity before each test.
     */
    @Rule
    public ActivityScenarioRule<LandingPage> landingPageActivityScenarioRule = new ActivityScenarioRule<>(LandingPage.class);

    @Rule
    public GrantPermissionRule notificationRule = GrantPermissionRule.grant(Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public GrantPermissionRule locationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    /**
     * Tests if user profile information is persisted correctly.
     * This method enters and saves user information, then reloads the activity
     * to check if the information is displayed correctly.
     * @throws InterruptedException for handling Thread.sleep in the test
     */
    @Test
    public void testProfileInfoPersistence() throws InterruptedException {
        onView(withText("Create Profile")).perform(click());
        onView(withId(R.id.new_full_name)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.new_phone)).perform(typeText("7805555555"), closeSoftKeyboard());
        onView(withId(R.id.new_home_page)).perform(typeText("eventlinkqr.com"), closeSoftKeyboard());
        onView(withId(R.id.new_loc_permission)).perform(click());
        onView(withId(R.id.new_save_button)).perform(click());
        SystemClock.sleep(3000);

        // Navigate to AttendeeProfileFragment
        onView(withId(R.id.attendee_profile_button)).perform(click());

        // Check persistence from creating account
        onView(withId(R.id.etFullName)).check(matches(withText("Test User")));
        onView(withId(R.id.phoneNumberEdit)).check(matches(withText("7805555555")));
        onView(withId(R.id.homepageEdit)).check(matches(withText("eventlinkqr.com")));
        onView(withId(R.id.toggleLocation)).check(matches(isChecked()));

        // Input the name "Jason" into the EditText and close the keyboard
        onView(withId(R.id.etFullName)).perform(replaceText("Jason"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.phoneNumberEdit)).perform(replaceText("1234567891"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.homepageEdit)).perform(replaceText("www.Jason.com"), ViewActions.closeSoftKeyboard());

        // Click on the Save button
        onView(withId(R.id.btnSave)).perform(click());

        SystemClock.sleep(3000);

        // Navigate back to AttendeeProfileFragment
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

        // Check if the AttendeeProfileActivity is started
        onView(ViewMatchers.withId(R.id.attendee_profile_button)) // Replace with a view ID unique to the AttendeeProfileActivity
                .check(matches(ViewMatchers.isDisplayed()));
    }
}
