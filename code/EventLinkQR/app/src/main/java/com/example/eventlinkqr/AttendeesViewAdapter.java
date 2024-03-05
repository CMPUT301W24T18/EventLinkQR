package com.example.eventlinkqr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * This class serves as a custom adapter for the tab layout on the attendees list page
 */
public class AttendeesViewAdapter extends FragmentStateAdapter{

    public AttendeesViewAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Create and return fragment instance for each tab
        return AttendeesTabFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        // Return the number of tabs
        return 3;
    }
}