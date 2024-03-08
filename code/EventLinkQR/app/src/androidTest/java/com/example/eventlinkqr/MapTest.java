package com.example.eventlinkqr;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import android.content.Intent;

import com.example.eventlinkqr.OrganizerEventStats;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapTest {

    /**
     * Check if the map container is displayed when the activity is launched
     * OpenAI, 2024, ChatGPT, Test the map with an intent
     */
    @Test
    public void mapIsDisplayed() {
        // Create an intent with the key-value pair
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OrganizerEventStats.class);
        intent.putExtra("eventId", "event123");

        // Launch the activity with the intent
        ActivityScenario.launch(intent);

        // Check if the map container is displayed
        Espresso.onView(ViewMatchers.withId(R.id.mapPreviewContainer))
                .check(matches(isDisplayed()));
    }
}
