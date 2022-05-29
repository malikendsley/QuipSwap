package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malikendsley.quipswap.navfragments.ProfileFragment;
import com.malikendsley.quipswap.navfragments.ReceivedFragment;
import com.malikendsley.quipswap.navfragments.SentFragment;
import com.malikendsley.quipswap.navfragments.SignInFragment;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Own";
    View customDialog;
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
    AlertDialog.Builder builder;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, true);

        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        customDialog = getLayoutInflater().inflate(R.layout.about_dialog, null);

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

        //dialog buttons
        Button emailMeButton = customDialog.findViewById(R.id.emailMeButton);
        Button linkedInButton = customDialog.findViewById(R.id.linkedInButton);

        linkedInButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.linkedin.com/in/malik-endsley/"));
            startActivity(intent);
        });

        emailMeButton.setOnClickListener(view -> {
            String mailto = "mailto:malik.s.endsley@gmail.com" +
                    "?cc=" +
                    "&subject=" + Uri.encode("Feedback About QuipSwap") +
                    "&body=" + Uri.encode("");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));

            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Error opening email app", Toast.LENGTH_SHORT).show();
            }
        });
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
                setupDialog();
                break;

            case R.id.settingsOption:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;

            case R.id.logoutOption:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, MainActivity.class));
                finish();

        }
        return true;
    }

    void setupDialog() {
        builder = new AlertDialog.Builder(this);
        builder.setView(customDialog);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}