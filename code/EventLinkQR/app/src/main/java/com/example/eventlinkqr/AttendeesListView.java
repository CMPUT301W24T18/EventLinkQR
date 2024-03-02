package com.example.eventlinkqr;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

/**
 * Class that creates the organizer view and keep-s track of events
 */
//https://www.youtube.com/watch?v=LXl7D57fgOQ
public class AttendeesListView extends AppCompatActivity {

    /** the tab layout that will allow to navigate though  all three lists of attendees*/
    TabLayout attendeesTabLayout;

    /** view pager that allows to swipe across the tabs*/
    ViewPager2 attendeesViewPager;

    /** the adapter that allows listviews to be displayed with a tab layout*/
    AttendeesViewAdapter attendeesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_view);
        attendeesTabLayout = findViewById(R.id.attendees_tab_layout);
        attendeesViewPager = findViewById(R.id.attendees_view_pager);
        attendeesViewAdapter = new AttendeesViewAdapter(this);

        attendeesViewPager.setAdapter(attendeesViewAdapter);

        // Sets the action when clicking a tab
        attendeesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                attendeesViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
}
