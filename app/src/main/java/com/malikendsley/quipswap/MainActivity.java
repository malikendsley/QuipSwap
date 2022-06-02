package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malikendsley.quipswap.navfragments.ProfileFragment;
import com.malikendsley.quipswap.navfragments.ReceivedFragment;
import com.malikendsley.quipswap.navfragments.SentFragment;
import com.malikendsley.quipswap.navfragments.SignInFragment;

public class MainActivity extends AppCompatActivity {

    //for first time startup
    private static final String PREFS_NAME = "com.malikendsley.quipswap.QuipWidget";
    final String PREF_VERSION_CODE_KEY = "version_code";
    final int DOESNT_EXIST = -1;

    //for menu buttons
    View aboutView;
    View legalView;
    AlertDialog aboutDialog;
    AlertDialog legalDialog;

    //firebase authentication
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //this activity contains the fragments that create the dashboard
    SentFragment sentFragment;
    ReceivedFragment recFragment;
    ProfileFragment loggedInFragment;
    SignInFragment signInFragment;

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = new ReceivedFragment();

        //when a navigation item is selected, set the selected fragment to the one whose ID is a match so that fragment can be displayed
        //resource IDs will be non-constant eventually, so if-else is necessary
        int itemId = item.getItemId();
        if (itemId == R.id.nav_received_swaps) {
            if (recFragment == null) {
                recFragment = new ReceivedFragment();
            }
            selectedFragment = recFragment;
        } else if (itemId == R.id.nav_sent_swaps) {
            if (sentFragment == null) {
                sentFragment = new SentFragment();
            }
            selectedFragment = sentFragment;
        } else if (itemId == R.id.nav_profile) {
            if (user != null) {
                if (loggedInFragment == null) {
                    loggedInFragment = new ProfileFragment();
                }
                //User is Logged in
                selectedFragment = loggedInFragment;
            } else {
                if (signInFragment == null) {
                    signInFragment = new SignInFragment();
                }
                //No User is Logged in
                selectedFragment = signInFragment;
            }
        }

        //place the selected fragment in the FrameView created under Layouts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    };


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        //does what it says on the tin
        displayTutorialIfFirstRun();

        //do not display the login button if the user is not signed in
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            invalidateOptionsMenu();
        }
        setContentView(R.layout.activity_main);

        //link the navigation buttons
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        //save tab on screen rotation (should never run, unless the user forces landscape)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SentFragment()).commit();
        }

        //dialog views
        aboutView = getLayoutInflater().inflate(R.layout.about_dialog, null);
        legalView = getLayoutInflater().inflate(R.layout.legal_dialog, null);

        //create alertdialogs
        AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(this);
        aboutBuilder.setView(aboutView);
        AlertDialog.Builder legalBuilder = new AlertDialog.Builder(this);
        legalBuilder.setView(legalView);
        aboutDialog = aboutBuilder.create();
        legalDialog = legalBuilder.create();

        //about dialog buttons
        Button emailMeButton = aboutView.findViewById(R.id.emailMeButton);
        Button linkedInButton = aboutView.findViewById(R.id.linkedInButton);

        //legal dialog buttons
        Button privacyPolicyButton = legalView.findViewById(R.id.privacyPolicyButton);
        Button dataInquiryButton = legalView.findViewById(R.id.dataInquiryButton);

        //button listeners
        privacyPolicyButton.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setData(Uri.parse("https://www.privacypolicies.com/live/c6452f28-3848-4b4a-8b8f-3798bcb59022"));
            startActivity(intent);
        });

        dataInquiryButton.setOnClickListener(view -> {
            String mailto = "mailto:malik.s.endsley@gmail.com" +
                    "?cc=" +
                    "&subject=" + Uri.encode("Data Inquiry - QuipSwap") +
                    "&body=" + Uri.encode("");
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse(mailto));

            try {
                startActivity(emailIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(this, "Error opening email app", Toast.LENGTH_SHORT).show();
            }
        });

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
            case R.id.mainHelpOption:
                //TODO literally any tutorial, please
                break;
            case R.id.legalOption:
                legalDialog.show();
                break;
            case R.id.aboutUsOption:
                aboutDialog.show();
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

    //this method also accounts for upgrades, so tutorial will re-display if a user persists their SharedPrefs via
    void displayTutorialIfFirstRun() {
        int currentVersionCode = BuildConfig.VERSION_CODE;
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        if (savedVersionCode == DOESNT_EXIST || currentVersionCode > savedVersionCode) {
            //new or upgraded user
            //TODO tutorial
            prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
        }
    }
}