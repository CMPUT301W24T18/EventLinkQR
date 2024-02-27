package com.example.eventlinkqr;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AttendeesViewAdapter extends FragmentStateAdapter {
    public AttendeesViewAdapter(@NonNull FragmentActivity fragmentactivity) {
        super(fragmentactivity);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:
                return new AllAttendeesFragment();
            case 1:
                return new CheckedAttendeesFragment();
            case 2:
                return new NotCheckedFragment();
            default:
                return new AllAttendeesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
