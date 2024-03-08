package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.os.SystemClock;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class OrgMainActivityTest {

    @Rule
    public ActivityScenarioRule<OrgMainActivity> scenario = new
            ActivityScenarioRule<OrgMainActivity>(OrgMainActivity.class);

    @Test
    public void testOrgHomePage(){
        //
        onView(withId(R.id.button3)).perform(click());
        // wait 2 seconds to make sure the page loads
        SystemClock.sleep(500);
        // verifies i got to the home page
        onView(withText("My Events")).check(matches(isDisplayed()));
    }

    @Test
    public void testswitchAccountBtn(){
        onView(withId(R.id.button3)).perform(click());
        // wait 2 seconds to make sure the page loads
        SystemClock.sleep(500);
        // verifies i got to the home page
        onView(withText("My Events")).check(matches(isDisplayed()));

        onView(withId(R.id.switch_account)).perform(click());

        // wait 2 seconds to make sure the page loads
        SystemClock.sleep(500);
        onView(withText("Choose a role")).check(matches(isDisplayed()));
    }
    @Test
    public void testAddEventErrorMessage(){
        // wait to make sure the page loads
        SystemClock.sleep(2000);
        // verifies i got to the home page
        onView(withText("My Events")).check(matches(isDisplayed()));
        // open the create event page
        onView(withId(R.id.create_event_button)).perform(click());
        // wait to make sure the page loads
        SystemClock.sleep(1000);
        // fill in inputs and select a category
        onView(withId(R.id.event_name_input)).perform(ViewActions.typeText("Tester event"));

        onView(withId(R.id.event_description_input)).perform(ViewActions.typeText("testing if the create event function works"));

        onView(withId(R.id.event_location_input)).perform(ViewActions.typeText("everywhere, anywhere all at once"));

//        onView(withId(R.id.category_selector)).perform(click());
//        // can't click on a selection
//        // stuck

    }
}
