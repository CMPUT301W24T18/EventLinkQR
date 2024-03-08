package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.swipeDown;
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
public class NotificationDisplayActivityTest {
    @Rule
    public ActivityScenarioRule<NotificationDisplayActivity> activityScenarioRule
            = new ActivityScenarioRule<>(NotificationDisplayActivity.class);

    @Test
    public void testSwipeToRefreshNotifications() {
        // Swipe down to refresh notifications
        onView(withId(R.id.swipeRefreshLayout)).perform(swipeDown());

    }

}
