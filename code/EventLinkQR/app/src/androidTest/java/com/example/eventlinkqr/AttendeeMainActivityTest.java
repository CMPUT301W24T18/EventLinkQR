package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AttendeeMainActivityTest {
    @Test
    public void openCamera() {
        try (ActivityScenario<AttendeeMainActivity> activityScenario = ActivityScenario.launch(AttendeeMainActivity.class)) {
            onView(withId(R.id.attendee_scan_button)).perform(click());

            // Need to evaluate if this test is possible (it involves the Google QR Code scanner)
        }
    }
}
