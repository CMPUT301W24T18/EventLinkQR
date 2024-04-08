package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.Intents.release;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasType;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.SystemClock;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class OrganizerIntentTest extends BaseIntentTest{

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
    public void testEventIntent() {
        init(); // For intent test later

        onView(withText("Create Profile")).perform(click());
        enterProfileDetails("Test User",  "7805555555", "eventlinkqr.com");
        SystemClock.sleep(3000);

        enterEventDetails("Intent Test Event", "Intent testing event", "123 456 Street, Cityville, Ohio");

        performActionWithRetryOnText("My Events", click(), 5, 1000);

        performCheckWithRetryOnText("Intent Test Event", isDisplayed(), 5, 1000);

        performActionWithRetryOnText("Intent Test Event", click(), 5, 1000);

        performCheckWithRetry(R.id.org_event_location, withText("123 456 Street, Cityville, Ohio"), 5, 1000);
        performCheckWithRetry(R.id.org_event_description, withText("Intent testing event"), 5, 1000);
        performCheckWithRetry(R.id.org_event_category, withText("Workshop"), 5, 1000);

        performActionWithRetry(R.id.edit_event_button, click(), 5, 2000);
        SystemClock.sleep(2000);
        onView(withId(R.id.event_name_input)).perform(replaceText("Changed Event Name"), ViewActions.closeSoftKeyboard());
        performActionWithRetry(R.id.publish_button, click(), 5, 2000);

        performActionWithRetryOnText("My Events", click(), 5, 1000);
        performCheckWithRetryOnText("Changed Event Name", isDisplayed(), 5, 1000);
        performActionWithRetryOnText("Changed Event Name", click(), 5, 1000);

        // Test QR code share intents and display
        // Check in
        performActionWithRetry(R.id.checkin_qr_button, click(), 5, 1000);
        performCheckWithRetry(R.id.view_qr_image, isDisplayed(), 5, 1000);
        // OpenAI, 2024, ChatGPT, Fix intent not working in intent test
        intending(hasAction(Intent.ACTION_SEND)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null));
        performActionWithRetry(R.id.share_qr_image, click(), 5, 1000);
        intended(allOf(
                hasAction(Intent.ACTION_SEND),
                hasType("image/png")
        ));

        pressBack();

        // Promotional
        performActionWithRetry(R.id.promotional_qr_button, click(), 5, 1000);
        performCheckWithRetry(R.id.view_qr_image, isDisplayed(), 5, 1000);
        performActionWithRetry(R.id.share_qr_image, click(), 5, 1000);
        pressBack();

        // Sign up and check that you're in the attendees list
        performCheckWithRetryOnText("Changed Event Name", isDisplayed(), 5, 1000);
        performActionWithRetry(R.id.view_as_attendee, scrollTo(), 5, 1000);
        performActionWithRetry(R.id.view_as_attendee, click(), 5, 1000);
        performActionWithRetry(R.id.sign_up_button, click(), 5, 1000);
        pressBack();
        pressBack();
        performActionWithRetryOnText("My Events", click(), 5, 1000);
        performActionWithRetryOnText("Changed Event Name", click(), 5, 1000);
        performActionWithRetry(R.id.attendees_button, scrollTo(), 5, 1000);
        performActionWithRetry(R.id.attendees_button, click(), 5, 1000);
        performCheckWithRetryOnText("Test User", isDisplayed(), 5, 1000);
        performActionWithRetryOnText("Not Checked In", click(), 5, 1000);
        performCheckWithRetryOnText("Test User", isDisplayed(), 5, 1000);
        performActionWithRetryOnText("Checked In", click(), 5, 1000);
        onView(withText("Test User")).check(doesNotExist());

        // Test notifications
        pressBack();
        performActionWithRetry(R.id.notification_send_button, click(), 5, 1000);
        performCheckWithRetryOnText("Changed Event Name", isDisplayed(), 10, 1000);
        performActionWithRetry(R.id.btnSendNotification, click(), 5, 1000);
        performActionWithRetry(R.id.etNotificationTitle, typeText("Notification Title"), 5, 1000);
        performActionWithRetry(R.id.etNotificationMessage, typeText("This is a test notification message"), 5, 1000);
        closeSoftKeyboard();
        performActionWithRetry(R.id.btnCreateNotification, click(), 5, 1000);
        performCheckWithRetryOnText("Notification Title", isDisplayed(), 5, 2000);
        performActionWithRetry(R.id.attendee_notification_button, click(), 5, 1000);
        performCheckWithRetryOnText("Notification Title", isDisplayed(), 5,1000);
        performActionWithRetryOnText("Notification Title", click(), 5,1000);
        performCheckWithRetryOnText("Changed Event Name", isDisplayed(), 10, 1000);

        release();
    }
}
