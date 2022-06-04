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
import androidx.core.content.res.ResourcesCompat;
import androidx.core.splashscreen.SplashScreen;
import androidx.fragment.app.Fragment;

import com.elconfidencial.bubbleshowcase.BubbleShowCase;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseBuilder;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseListener;
import com.elconfidencial.bubbleshowcase.BubbleShowCaseSequence;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.malikendsley.quipswap.navfragments.ProfileFragment;
import com.malikendsley.quipswap.navfragments.ReceivedFragment;
import com.malikendsley.quipswap.navfragments.SentFragment;
import com.malikendsley.quipswap.navfragments.SignInFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    //nav
    BottomNavigationView bottomNav;
    int currentFragment = 0;

    //for menu buttons
    View aboutView;
    View legalView;
    AlertDialog aboutDialog;
    AlertDialog legalDialog;

    //firebase authentication
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //bubbles
    BubbleShowCaseBuilder BSC1 = new BubbleShowCaseBuilder(this);
    BubbleShowCaseBuilder BSC2 = new BubbleShowCaseBuilder(this);
    BubbleShowCaseBuilder BSC3 = new BubbleShowCaseBuilder(this);
    BubbleShowCaseBuilder BSC4 = new BubbleShowCaseBuilder(this);
    BubbleShowCaseBuilder BSC5 = new BubbleShowCaseBuilder(this);

    boolean showHelp = true;

    //this activity contains the fragments that create the dashboard
    Fragment selectedFragment;

    Fragment sentFragment;
    Fragment recFragment;
    Fragment loggedInFragment;
    Fragment signInFragment;

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        //when a navigation item is selected, set the selected fragment to the one whose ID is a match so that fragment can be displayed
        //resource IDs will be non-constant eventually, so if-else is necessary
        int itemId = item.getItemId();
        if (itemId == R.id.nav_received_swaps) {
            selectedFragment = recFragment;
            currentFragment = 0;
            showHelp = true;
        } else if (itemId == R.id.nav_sent_swaps) {
            selectedFragment = sentFragment;
            currentFragment = 1;
            showHelp = true;
        } else if (itemId == R.id.nav_profile) {
            if (user != null) {
                //User is Logged in
                selectedFragment = loggedInFragment;
            } else {
                //No User is Logged in
                selectedFragment = signInFragment;
            }
            currentFragment = 2;
            showHelp = false;
        }

        //place the selected fragment in the FrameView created under Layouts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        invalidateOptionsMenu();
        return true;
    };


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashScreen.installSplashScreen(this);

        //set up fragments (the bubble system necessitates this)
        recFragment = new ReceivedFragment();
        sentFragment = new SentFragment();
        loggedInFragment = new ProfileFragment();
        signInFragment = new SignInFragment();
        selectedFragment = sentFragment;


        //do not display the login button if the user is not signed in
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            invalidateOptionsMenu();
        }
        setContentView(R.layout.activity_main);

        //link the navigation buttons
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

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

        //does what it says on the tin
        displayTutorialIfFirstRun();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.logoutOption).setVisible(user != null);
        menu.findItem(R.id.mainHelpOption).setVisible(showHelp);
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
                if (currentFragment == 0) {
                    initBubbles();
                } else if (currentFragment == 1) {
                    initBubbles();
                }

                new BubbleShowCaseSequence().addShowCase(BSC1).addShowCase(BSC2).addShowCase(BSC3).addShowCase(BSC4).addShowCase(BSC5).show();
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

        final String PREFS_NAME = "MyPrefsFile";

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            initBubbles();
            new BubbleShowCaseSequence().addShowCase(BSC1).addShowCase(BSC2).addShowCase(BSC3).addShowCase(BSC4).addShowCase(BSC5).show();
            // first time task

            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).apply();
        }
    }

    void initBubbles() {
        BSC1.title("Welcome to QuipSwap!").listener(new BubbleShowCaseListener() {
            @Override
            public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                bubbleShowCase.dismiss();
            }

            @Override
            public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
            }
        });

        BSC2.title("Use these buttons to navigate the dashboard!").targetView(bottomNav).listener(new BubbleShowCaseListener() {
            @Override
            public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                bubbleShowCase.dismiss();
            }

            @Override
            public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
            }
        });
        BSC3.title("Tap here to create a Quip!").targetView(findViewById(R.id.fab)).description("Note that you must be logged in to share them.").listener(new BubbleShowCaseListener() {
            @Override
            public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                bubbleShowCase.dismiss();
            }

            @Override
            public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
            }
        });
        BSC4.title("Tap the three dots at the upper right corner for more options!").listener(new BubbleShowCaseListener() {
            @Override
            public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
            }

            @Override
            public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                bubbleShowCase.dismiss();
            }

            @Override
            public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
            }
        });
        View view = findViewById(R.id.mainHelpOption);
        if (view == null) {
            BSC5 = new BubbleShowCaseBuilder(this).title("Tap this icon at the top of the screen to see this guide again!").image(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_help_outline_24, null))).listener(new BubbleShowCaseListener() {
                @Override
                public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
                }

                @Override
                public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
                }

                @Override
                public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                    bubbleShowCase.dismiss();
                }

                @Override
                public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                }
            });
        } else {
            BSC5 = new BubbleShowCaseBuilder(this).title("Tap here to see this guide again!").targetView(view).listener(new BubbleShowCaseListener() {
                @Override
                public void onTargetClick(@NonNull BubbleShowCase bubbleShowCase) {
                }

                @Override
                public void onCloseActionImageClick(@NonNull BubbleShowCase bubbleShowCase) {
                }

                @Override
                public void onBackgroundDimClick(@NonNull BubbleShowCase bubbleShowCase) {
                    bubbleShowCase.dismiss();
                }

                @Override
                public void onBubbleClick(@NonNull BubbleShowCase bubbleShowCase) {
                }
            });
        }
    }
}