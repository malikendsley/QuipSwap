package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malikendsley.quipswap.navfragments.ProfileFragment;
import com.malikendsley.quipswap.navfragments.ReceivedFragment;
import com.malikendsley.quipswap.navfragments.SentFragment;
import com.malikendsley.quipswap.navfragments.SignInFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Own";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    SentFragment sentFragment;
    ReceivedFragment recFragment;
    ProfileFragment loggedInFragment;
    SignInFragment signInFragment;

    @SuppressLint("NonConstantResourceId")
    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = new ReceivedFragment();

        //when a navigation item is selected, set the selected fragment to the one whose ID is a match so that fragment can be displayed
        switch (item.getItemId()) {
            case R.id.nav_received_swaps:
                Log.i(TAG, "Received selected");
                if (recFragment == null) {
                    //Log.i(TAG, "Generating");
                    recFragment = new ReceivedFragment();
                }
                selectedFragment = recFragment;
                break;
            case R.id.nav_sent_swaps:
                Log.i(TAG, "Sent selected");
                if (sentFragment == null) {
                    //Log.i(TAG, "Generating");
                    sentFragment = new SentFragment();
                }
                selectedFragment = sentFragment;
                break;
            case R.id.nav_profile:
                Log.i(TAG, "Profile Selected");
                //send the right fragment based on whether user is logged in
                if (user != null) {
                    if (loggedInFragment == null) {
                        //Log.i(TAG, "Generating");
                        loggedInFragment = new ProfileFragment();
                    }
                    //User is Logged in
                    Log.d(TAG, "onNavigationItemSelected User logged in");
                    selectedFragment = loggedInFragment;
                } else {
                    if (signInFragment == null) {
                        //Log.i(TAG, "Generating");
                        signInFragment = new SignInFragment();
                    }
                    Log.d(TAG, "onNavigationItemSelected No user logged in");
                    //No User is Logged in
                    selectedFragment = signInFragment;
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
        SplashScreen.installSplashScreen(this);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            invalidateOptionsMenu();
        }
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);
        //try to run this as late as possible to check whether to display friends tab
        //save tab on screen rotation
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SentFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.logoutOption).setVisible(user != null);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.friendRequestsAppBar:
                if (user != null) {
                    //Log.i(TAG, "");
                    startActivity(new Intent(this, FriendRequestsActivity.class));
                } else {
                    if (signInFragment == null) {
                        signInFragment = new SignInFragment();
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, signInFragment).commit();
                    Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.aboutUsOption:
                //unlikely but if this presents a perf issue can pre-build
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.about_us).setMessage(R.string.about_us_text).setCancelable(true).show();
                break;
            case R.id.settingsOption:
                Snackbar.make(findViewById(android.R.id.content), "Coming Soon", Snackbar.LENGTH_SHORT).show();
                break;
            case R.id.logoutOption:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();
        }
        return true;
    }
}