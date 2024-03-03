package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;

/**
 * Class that creates the organizer view and keep-s track of events
 */
//https://www.youtube.com/watch?v=LXl7D57fgOQ
public class OrgAttendeesPage extends Fragment {

    /**
     * the tab layout that will allow to navigate though  all three lists of attendees
     */
    TabLayout attendeesTabLayout;

    /**
     * view pager that allows to swipe across the tabs
     */
    ViewPager2 attendeesViewPager;

    Toolbar attendeesToolBar;
    /**
     * the adapter that allows listviews to be displayed with a tab layout
     */
    AttendeesViewAdapter attendeesViewAdapter;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.org_attendees_page, container, false);
        attendeesTabLayout = view.findViewById(R.id.attendees_tab_layout);
        attendeesViewPager = view.findViewById(R.id.attendees_view_pager);
        //attendeesViewAdapter = new AttendeesViewAdapter(this);
        attendeesToolBar = (Toolbar) view.findViewById(R.id.org_attendees_tool_bar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(attendeesToolBar);

        attendeesToolBar.setTitle(null);

        attendeesViewPager.setAdapter(attendeesViewAdapter);

        attendeesToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_attendeesPage_to_orgEventFragment);
            }
        });
        /** Sets the action when clicking a tab*/
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
        // Inflate the layout for this fragment
        return view;
    }
}