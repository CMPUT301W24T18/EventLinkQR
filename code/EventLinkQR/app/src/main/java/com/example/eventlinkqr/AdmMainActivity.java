package com.example.eventlinkqr;
import com.example.eventlinkqr.AttendeeProfileActivity;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.eventlinkqr.databinding.AdmMainActivityBinding;

public class AdmMainActivity extends AppCompatActivity {

    AdmMainActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = AdmMainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new AdminEventsFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.users) {
                replaceFragment(new AdminUsersFragment());
            } else if (id == R.id.events) {
                replaceFragment(new AdminEventsFragment());
            } else if (id == R.id.profile) {
                Intent intent = new Intent(this, AttendeeMainActivity.class);
                startActivity(intent);
            } else if (id == R.id.images) {
                replaceFragment(new AdminImagesFragment());
            }
            return true;
        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();

    }
}
