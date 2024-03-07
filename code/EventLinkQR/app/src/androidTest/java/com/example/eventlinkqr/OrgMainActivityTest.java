package com.example.eventlinkqr;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


import android.os.SystemClock;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class OrgMainActivityTest {

    @Rule
    public ActivityScenarioRule<OrgMainActivity> scenario = new
            ActivityScenarioRule<OrgMainActivity>(OrgMainActivity.class);

    @Test
    public void testLogin(){
        // enter email and password
        onView(withId(R.id.org_email_address)).perform(ViewActions.typeText("team18@ualberta.ca"));
        onView(withId(R.id.org_password)).perform(ViewActions.typeText("123"));
        onView(withId(R.id.org_signin_button)).perform(click());
        // wait 2 seconds to make sure the page loads
        SystemClock.sleep(2000);
        // verifies i got to the home page
        onView(withText("My Events")).check(matches(isDisplayed()));
    }

    @Test
    public void testAddEvent(){
        // log in to access the create event function
        onView(withId(R.id.org_email_address)).perform(ViewActions.typeText("team18@ualberta.ca"));
        onView(withId(R.id.org_password)).perform(ViewActions.typeText("123"));
        onView(withId(R.id.org_signin_button)).perform(click());
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

        onView(withId(R.id.category_selector)).perform(click());
        // can't click on a selection
        // stuck


        onView(withId(R.id.publish_button)).perform(click());
        // wait to make sure the page loads
        SystemClock.sleep(1000);
        // verify that the new event appears on the home page
        onView(withText("Tester event")).check(matches(isDisplayed()));
    }
}
