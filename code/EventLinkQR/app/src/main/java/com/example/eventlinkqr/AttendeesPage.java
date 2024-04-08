package com.example.eventlinkqr;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Class that displays the list of attendees for an event
 */
//https://www.youtube.com/watch?v=LXl7D57fgOQ
public class AttendeesPage extends Fragment  {

    /** the title for each tab*/
    private static final String[] TAB_TITLES = {"All", "Checked In", "Not Checked In"};

    private Event event;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.attendees_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        event = ((UserMainActivity) requireActivity()).getCurrentEvent();

        // the tool bar on top of the page
        Toolbar orgAttendeesToolbar = view.findViewById(R.id.org_attendees_tool_bar);

        // the tab layout that will allow to navigate though  all three lists of attendees
        TabLayout attendeesTabLayout = view.findViewById(R.id.attendees_tab_layout);

        // view pager that allows to swipe across the tabs
        ViewPager2 attendeesViewPager = view.findViewById(R.id.attendees_view_pager);

        // make the back button return to the event page
        ((AppCompatActivity) requireActivity()).setSupportActionBar(orgAttendeesToolbar);
        orgAttendeesToolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_orgAttendeesPage_to_orgEventFragment));
        orgAttendeesToolbar.setTitle(null);

        AttendeesViewAdapter pagerAdapter = new AttendeesViewAdapter(requireActivity());
        attendeesViewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(attendeesTabLayout, attendeesViewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }
}