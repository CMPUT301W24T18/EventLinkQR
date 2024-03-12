package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HomePage extends Fragment {

    /** the title for each tab*/
    private static final String[] TAB_TITLES = {"All", "My Events", "My Upcoming Events"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // the tab layout that will allow to navigate though  all three lists of attendees
        TabLayout attendeesTabLayout = view.findViewById(R.id.events_tab_layout);

        // view pager that allows to swipe across the tabs
        ViewPager2 eventsViewpager = view.findViewById(R.id.events_view_pager);

        EventsViewAdapter pagerAdapter = new EventsViewAdapter(requireActivity());
        eventsViewpager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(attendeesTabLayout, eventsViewpager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }
}
