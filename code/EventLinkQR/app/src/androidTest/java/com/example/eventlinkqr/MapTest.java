package com.example.eventlinkqr;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import com.example.eventlinkqr.OrganizerEventStats;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MapTest {

    /**
     * Check if the map container is displayed when the activity is launched
     */
    @Test
    public void mapIsDisplayed() {
        ActivityScenario.launch(OrganizerEventStats.class);

        Espresso.onView(ViewMatchers.withId(R.id.mapPreviewContainer))
                .check(matches(isDisplayed()));
    }
}
