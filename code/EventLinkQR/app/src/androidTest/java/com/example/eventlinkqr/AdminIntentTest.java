package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.Manifest;
import android.os.SystemClock;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Intent tests for the Admin functionality
 */
@RunWith(AndroidJUnit4.class)
public class AdminIntentTest {
    /**
     * Rule to launch the AttendeeMainActivity before each test.
     */
    @Rule
    public ActivityScenarioRule<LandingPage> landingPageActivityScenarioRule = new ActivityScenarioRule<>(LandingPage.class);

    @Rule
    public GrantPermissionRule notificationRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    @Rule
    public GrantPermissionRule locationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    @Test
    public void testOpenAdminMode() {
        // Create a new profile
        onView(withText("Create Profile")).perform(click());
        onView(withId(R.id.new_full_name)).perform(typeText("Test User"), closeSoftKeyboard());
        onView(withId(R.id.new_phone)).perform(typeText("7805555555"), closeSoftKeyboard());
        onView(withId(R.id.new_home_page)).perform(typeText("eventlinkqr.com"), closeSoftKeyboard());
        onView(withId(R.id.new_loc_permission)).perform(click());
        onView(withId(R.id.new_save_button)).perform(click());

        SystemClock.sleep(3000);

        // Click the profile button 10 times
        for (int i = 0; i <= 10; i++) {
            onView(withId(R.id.attendee_profile_button)).perform(click());
        }

        // Enter the pin (in a production environment, we would not have the pin hard-coded
        onView(withId(R.id.pinEditText)).check(matches(isDisplayed())).perform(typeText("1010")).perform(closeSoftKeyboard());
        onView(withId(R.id.submitPinButton)).perform(click());
        onView(withId(R.id.tvEventHeader)).check(matches(withText("Events")));

        // Navigate to the users tab
        onView(withId(R.id.users)).perform(click());
        onView(withId(R.id.tvEventHeader)).check(matches(withText("Users\n")));

        // Navigate to the images tab
        onView(withId(R.id.images)).perform(click());
        onView(withId(R.id.listViewImages)).check(matches(isDisplayed()));

        // Go back to the attendee activity
        onView(withId(R.id.profile)).perform(click());
        onView(withText("YES")).perform(click());
        onView(withText("Events")).check(matches(isDisplayed()));

        // Reopen admin mode
        for (int i = 0; i <= 10; i++) {
            onView(withId(R.id.attendee_profile_button)).perform(click());
        }

        // Ensure we are taken straight to the admin stuff, as we have already entered the pin.
        onView(withId(R.id.tvEventHeader)).check(matches(withText("Events")));
    }
}
