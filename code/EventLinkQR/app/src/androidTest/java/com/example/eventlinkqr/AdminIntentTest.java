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
import android.view.View;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso based intent tests for the Admin functionality in the Event Link QR application.
 * This class extends {@link BaseIntentTest} and focuses on testing the interactions and navigation flows
 * an admin user would typically perform. It includes tests for profile creation, user and event management,
 * and navigation within the admin interface.
 *
 * Permissions for posting notifications and accessing fine location are granted for these tests.
 */
@RunWith(AndroidJUnit4.class)
public class AdminIntentTest extends BaseIntentTest{
    @Rule
    public GrantPermissionRule notificationRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public GrantPermissionRule locationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    /**
     * Tests various user stories and functionalities available to an admin user.
     * This includes creating a new profile, performing operations like pin entry, navigating to different
     * tabs like users and images, managing events and attendees, and verifying the correct UI elements are displayed.
     * The test ensures that all the critical admin functionalities are working as expected.
     */
    @Test
    public void testAdminUserStories() {
        // Create a new profile
        onView(withText("Create Profile")).perform(click());
        SystemClock.sleep(2500);

        enterProfileDetails("Aaa Admin User",  "7805555555", "homepage.com");
        performActionWithRetry(R.id.attendee_profile_button, click(), 5, 2000);

        // Click the profile button 10 times
        for (int i = 0; i < 10; i++) {
            onView(withId(R.id.attendee_profile_button)).perform(click());
        }

        performActionWithRetry(R.id.pinEditText, click(), 5, 2000);
        // Enter the pin (in a production environment, we would not have the pin hard-coded
        onView(withId(R.id.pinEditText)).check(matches(isDisplayed())).perform(typeText("1010")).perform(closeSoftKeyboard());
        onView(withId(R.id.submitPinButton)).perform(click());

        // Navigate to the users tab
        performActionWithRetry(R.id.users, click(), 5, 2000);
        performCheckWithRetry(R.id.tvEventHeader, withText("Users\n"), 5, 1000); // 5 attempts, 1-second delay


        // Navigate to the images tab
        performActionWithRetry(R.id.images, click(), 5, 2000);
        SystemClock.sleep(3500);
        onView(withId(R.id.listViewImages)).check(matches(isDisplayed()));

        // Go back to the attendee activity
        onView(withId(R.id.profile)).perform(click());
        performActionWithRetryOnText("YES", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay
        performCheckWithRetryOnText("Events", isDisplayed(), 5, 1000); // Check if "Events" is displayed, 5 attempts with 1-second delay

        enterEventDetails("Admin Test Event", "Intent testing event", "123 456 Street, Cityville, Ohio");

        // Reopen admin mode
        for (int i = 0; i <= 10; i++) {
            onView(withId(R.id.attendee_profile_button)).perform(click());
        }

        // Deleting the event
        performCheckWithRetry(R.id.tvEventHeader, withText("Events"), 5, 1000); // 5 attempts, 1-second delay
        performActionWithRetryOnText("Admin Test Event", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay
        performActionWithRetry(R.id.deleteEventButton, click(), 5, 2000);
        performActionWithRetryOnText("OK", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay

        // Deleting the user
        performActionWithRetry(R.id.users, click(), 5, 2000);
        performCheckWithRetry(R.id.tvEventHeader, withText("Users\n"), 5, 1000); // 5 attempts, 1-second delay
        performActionWithRetryOnText("Aaa Admin User", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay
        performActionWithRetry(R.id.deleteAttendeeButton, click(), 5, 2000);
        performActionWithRetryOnText("OK", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay


        // Go back to the attendee activity
        performActionWithRetry(R.id.profile, click(), 5, 2000);
        performActionWithRetryOnText("YES", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay
        performCheckWithRetryOnText("Events", isDisplayed(), 5, 1000); // Check if "Events" is displayed, 5 attempts with 1-second delay
    }

}