package com.malikendsley.quipswap;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private static final String TAG = "Own";

    @SuppressLint("NonConstantResourceId")
    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = new ReceivedFragment();

        //when a navigation item is selected, set the selected fragment to the one whose ID is a match so that fragment can be displayed
        switch (item.getItemId()) {
            case R.id.nav_received_swaps:
                Log.i(TAG, "Received selected");
                selectedFragment = new ReceivedFragment();
                break;
            case R.id.nav_sent_swaps:
                Log.i(TAG, "Sent selected");
                selectedFragment = new SentFragment();
                break;
            case R.id.nav_profile:
                Log.i(TAG, "Profile Selected");
                //send the right fragment based on whether user is logged in
                if (user != null) {
                    //User is Logged in
                    Log.d("Firebase", "onNavigationItemSelected User logged in");
                    selectedFragment = new ProfileLoggedInFragment();
                } else {
                    Log.d("Firebase", "onNavigationItemSelected No user logged in");
                    //No User is Logged in
                    selectedFragment = new ProfileFragment();
                }
                break;
        }

        //place the selected fragment in the FrameView created under Layouts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check user login state
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SentFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.mainoverflowmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.aboutUsOption:
                //unlikely but if this presents a perf issue can pre-build
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.about_us).setMessage(R.string.about_us_text).setCancelable(true).show();
                break;
            case R.id.settingsOption:
                Snackbar.make(findViewById(android.R.id.content), "Coming Soon", Snackbar.LENGTH_SHORT).show();
                break;

        }
        return true;
    }
}