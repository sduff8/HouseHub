package com.example.househub;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Objects.requireNonNull(getSupportActionBar()).hide();
        //getWindow().addFlags((WindowManager.LayoutParams.FLAG_FULLSCREEN));

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new HomeFragment()).commit();
        bottomNavigationView.setSelectedItemId(R.id.homeFragment);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            switch (item.getItemId()){

                case R.id.homeFragment:
                    fragment = new HomeFragment();
                    break;

                case R.id.chatFragment:
                    fragment = new ChatFragment();
                    break;

                case R.id.galleryFragment:
                    fragment = new GalleryFragment();
                    break;

                case R.id.calendarFragment:
                    fragment = new CalendarFragment();
                    break;

                case R.id.contactsFragment:
                    fragment = new ContactsFragment();
                    break;
            }
            assert fragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, fragment).commit();

            return true;
        });
    }
}