package com.malikendsley.quipswap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        //when a navigation item is selected, set the selected fragment to the one whose ID is a match so that fragment can be displayed
        switch (item.getItemId()) {
            case R.id.nav_received_swaps:
                selectedFragment = new ReceivedFragment();
                break;
            case R.id.nav_sent_swaps:
                selectedFragment = new SentFragment();
                break;
            case R.id.nav_profile:
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

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SentFragment()).commit();
    }

    public void onSignupPressed(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }
    public void onLoginPressed(){

    }
}