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
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * Class that creates the organizer view and keep-s track of events
 */
//https://www.youtube.com/watch?v=LXl7D57fgOQ
public class OrgAttendeesPage extends Fragment {

    private static final String[] TAB_TITLES = {"All", "Checked In", "Not Checked In"};
    private static final String ARG_TAB_POSITION = "TAB_POSITION";

    @NonNull
    public static OrgAttendeesPage newInstance(int tabPosition) {
        OrgAttendeesPage fragment = new OrgAttendeesPage();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_POSITION, tabPosition);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.org_attendees_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // the tab layout that will allow to navigate though  all three lists of attendees
        TabLayout attendeesTabLayout = view.findViewById(R.id.attendees_tab_layout);

        // view pager that allows to swipe across the tabs
        ViewPager2 attendeesViewPager = view.findViewById(R.id.attendees_view_pager);

        AttendeesViewAdapter pagerAdapter = new AttendeesViewAdapter(requireActivity());
        attendeesViewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(attendeesTabLayout, attendeesViewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])
        ).attach();
    }

}