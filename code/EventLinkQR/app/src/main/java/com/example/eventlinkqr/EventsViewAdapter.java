package com.example.eventlinkqr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * custom view adapter for the different event pages
 */
public class EventsViewAdapter extends FragmentStateAdapter {

    public EventsViewAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create and return fragment instance for each tab
        return EventsTabFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        // Return the number of tabs
        return 3;
    }
}