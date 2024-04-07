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
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Matcher;
import org.junit.Rule;
/**
 * An abstract base class for intent testing in the Event Link QR application.
 * This class provides common setup and utility methods for Espresso tests. It includes
 * rules for launching activities, granting permissions, and helper methods for common actions
 * such as entering profile and event details, and retrying actions.
 */
public abstract class BaseIntentTest {
    /**
     * Rule to launch the LandingPage activity before each test.
     * This ensures that each test starts from the LandingPage.
     */
    @Rule
    public ActivityScenarioRule<LandingPage> landingPageActivityScenarioRule = new ActivityScenarioRule<>(LandingPage.class);

    /**
     * Rule to grant permission for posting notifications.
     */
    @Rule
    public GrantPermissionRule notificationRule = GrantPermissionRule.grant(android.Manifest.permission.POST_NOTIFICATIONS);

    /**
     * Rule to grant permission for accessing fine location.
     */
    @Rule
    public GrantPermissionRule locationRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION);

    /**
     * Enters profile details into the UI.
     *
     * @param name     The name to be entered.
     * @param phone    The phone number to be entered.
     * @param homepage The homepage URL to be entered.
     */
    protected void enterProfileDetails(String name, String phone, String homepage) {
        SystemClock.sleep(1000);
        onView(withId(R.id.new_full_name)).perform(typeText(name), closeSoftKeyboard());
        onView(withId(R.id.new_phone)).perform(typeText(phone), closeSoftKeyboard());
        onView(withId(R.id.new_home_page)).perform(typeText(homepage), closeSoftKeyboard());
        onView(withId(R.id.new_loc_permission)).perform(click());
        onView(withId(R.id.new_save_button)).perform(click());
    }

    /**
     * Enters event details into the UI.
     *
     * @param eventName    The name of the event.
     * @param description  The description of the event.
     * @param location     The location of the event.
     */
    protected void enterEventDetails(String eventName, String description, String location){
        performActionWithRetryOnText("My Events", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay
        onView(allOf(ViewMatchers.withId(R.id.create_event_button), ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE))).perform(click());

        onView(ViewMatchers.withId(R.id.event_location_input)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.event_name_input)).perform(typeText(eventName));
        onView(ViewMatchers.withId(R.id.event_description_input)).perform(typeText(description));
        onView(ViewMatchers.withId(R.id.event_description_input)).perform(closeSoftKeyboard());
        onView(withId(R.id.new_event_geo_switch)).perform(click());
        onView(withId(R.id.category_selector)).perform(click());

        performActionWithRetryOnText("Workshop", click(), 5, 1000); // Attempts to click "Aaa Admin", 5 attempts with 1-second delay
        onView(ViewMatchers.withId(R.id.event_location_input)).perform(typeText(location));
        onView(ViewMatchers.withId(R.id.date_picker)).perform(click());
        onView(withText("OK")).perform(click());
        onView(withText("OK")).perform(click());
        performActionWithRetry(R.id.publish_button, click(), 5, 2000);
    }

    /**
     * Performs a given action on a view identified by its ID, with retry logic.
     *
     * @param viewId                 The ID of the view on which to perform the action.
     * @param action                 The action to be performed.
     * @param maxAttempts            The maximum number of attempts for the action.
     * @param delayBetweenAttempts   The delay in milliseconds between attempts.
     */
    protected void performActionWithRetry(int viewId, ViewAction action, int maxAttempts, long delayBetweenAttempts) {
        NoMatchingViewException lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                onView(withId(viewId)).perform(action);
                return;
            } catch (NoMatchingViewException e) {
                lastException = e;
                SystemClock.sleep(delayBetweenAttempts);
            }
        }
        throw lastException;
    }

    /**
     * Performs a check on a view identified by its ID, with retry logic.
     *
     * @param viewId                 The ID of the view to be checked.
     * @param users            The matcher to verify the view against.
     * @param maxAttempts            The maximum number of attempts for the check.
     * @param delayBetweenAttempts   The delay in milliseconds between attempts.
     */
    protected void performCheckWithRetry(int viewId, Matcher<View> users, int maxAttempts, int delayBetweenAttempts) {
        NoMatchingViewException lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                onView(withId(viewId)).check(matches(users));
                return;
            } catch (NoMatchingViewException | AssertionError e) {
                lastException = (NoMatchingViewException) e;
                SystemClock.sleep(delayBetweenAttempts);
            }
        }
        throw lastException;
    }

    /**
     * Performs a given action on a view identified by its text content, with retry logic.
     *
     * @param text                   The text of the view on which to perform the action.
     * @param action                 The action to be performed.
     * @param maxAttempts            The maximum number of attempts for the action.
     * @param delayBetweenAttempts   The delay in milliseconds between attempts.
     */
    protected void performActionWithRetryOnText(String text, ViewAction action, int maxAttempts, long delayBetweenAttempts) {
        NoMatchingViewException lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                onView(withText(text)).perform(action);
                return; // Success, so return early
            } catch (NoMatchingViewException e) {
                lastException = e;
                SystemClock.sleep(delayBetweenAttempts);
            }
        }
        throw lastException; // Throw the exception if all attempts fail
    }

    /**
     * Performs a check on a view identified by its text content, with retry logic.
     *
     * @param text                   The text content of the view to be checked.
     * @param viewMatcher            The matcher to verify the view against.
     * @param maxAttempts            The maximum number of attempts for the check.
     * @param delayBetweenAttempts   The delay in milliseconds between attempts.
     */
    protected void performCheckWithRetryOnText(String text, Matcher<View> viewMatcher, int maxAttempts, long delayBetweenAttempts) {
        NoMatchingViewException lastException = null;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                onView(withText(text)).check(matches(viewMatcher));
                return; // Success, so return early
            } catch (NoMatchingViewException | AssertionError e) {
                lastException = (NoMatchingViewException) e;
                SystemClock.sleep(delayBetweenAttempts);
            }
        }
        throw lastException; // Throw the exception if all attempts fail
    }
}