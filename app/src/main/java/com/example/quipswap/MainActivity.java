package com.example.quipswap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navlistener);

        if (savedInstanceState == null)getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SentFragment()).commit();
    }

    private NavigationBarView.OnItemSelectedListener navlistener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item){
            Fragment selectedFragment = null;
            //when a navigation item is selected, set the selected fragment to the one whose ID is a match so that fragment can be displayed
            switch(item.getItemId()){
                case R.id.nav_received_swaps:
                    selectedFragment = new ReceivedFragment();
                    break;
                case R.id.nav_sent_swaps:
                    selectedFragment = new SentFragment();
                    break;
                case R.id.nav_profile:
                    selectedFragment = new ProfileFragment();
                    break;
            }
            //place the selected fragment in the FrameView created under Layouts
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };
}