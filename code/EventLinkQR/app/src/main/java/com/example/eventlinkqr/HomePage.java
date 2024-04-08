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

/**
 * this class handles the home page of the app
 */
public class HomePage extends Fragment {

    /** the title for each tab*/
    private static final String[] TAB_TITLES = {"All", "My Events", "My Upcoming Events"};

    /**
     * Called to do initial creation of the fragment.
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_page, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has returned, but before any saved state has been restored in to the view.
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
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
