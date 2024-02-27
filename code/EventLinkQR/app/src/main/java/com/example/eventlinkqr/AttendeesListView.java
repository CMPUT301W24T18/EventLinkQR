package com.example.eventlinkqr;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
// source
//https://www.youtube.com/watch?v=LXl7D57fgOQ
public class AttendeesListView extends AppCompatActivity {

    TabLayout attendeesTabLayout;
    ViewPager2 attendeesViewPager;
    AttendeesViewAdapter attendeesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendees_view);
        attendeesTabLayout = findViewById(R.id.attendees_tab_layout);
        attendeesViewPager = findViewById(R.id.attendees_view_pager);
        attendeesViewAdapter = new AttendeesViewAdapter(this);

        attendeesViewPager.setAdapter(attendeesViewAdapter);

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
