package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.secureadapters.SecureFriendAdapter;
import com.malikendsley.quipswap.databinding.QuipWidgetConfigureBinding;
import com.malikendsley.quipswap.navfragments.SignInFragment;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link QuipWidget QuipWidget} AppWidget.
 */
public class QuipWidgetConfigureActivity extends AppCompatActivity {

    private static final String TAG = "Own";
    private static final String PREFS_NAME = "com.malikendsley.quipswap.QuipWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    RecyclerView friendRecycler;
    SecureFriendAdapter friendAdapter;
    ArrayList<String> friendList = new ArrayList<>();
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    String mFriendUID;
    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), QuipWidgetConfigureActivity.this);

    public QuipWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveFriendUIDPref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadFriendUIDPref(Context context, int appWidgetId) {
        //Log.i(TAG, "loadFriendUID called");
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String friendUID = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (friendUID != null) {
            return friendUID;
        } else {
            //Log.i(TAG, "friend UID is null");
            return "WAIT";
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        //with this, we will back out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        //bind
        com.malikendsley.quipswap.databinding.QuipWidgetConfigureBinding binding = QuipWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Log.i(TAG, "QuipWidgetConfigure: User Not Logged In, Redirecting");

            //make the empty frame visible and display a login fragment
            binding.configureLoginContainer.setVisibility(View.VISIBLE);
            Fragment mFragment;
            mFragment = new SignInFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.configureLoginContainer, mFragment).commit();

        } else {
            //make normal view visible and set up button
            binding.sourceSelectionLayout.setVisibility(View.VISIBLE);
            binding.addButton.setOnClickListener(view -> finish());

            // Find the widget id from the intent.
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mAppWidgetId = extras.getInt(
                        AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            }

            // If this activity was started with an intent without an app widget ID, finish with an error.
            if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
                finish();
                Log.i(TAG, "QuipConfigure: No appwidget ID");
                return;
            }

            mdb2.getFriends(new FriendRetrieveListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onGetFriends(ArrayList<String> friendUIDList) {
                    if (friendUIDList != null) {
                        friendList.clear();
                        friendList.addAll(friendUIDList);
                    }
                    friendAdapter.notifyDataSetChanged();
                }

                @Override
                public void onGetFailed(Exception e) {
                    e.printStackTrace();
                    Toast.makeText(QuipWidgetConfigureActivity.this, "Having trouble connecting to the database", Toast.LENGTH_SHORT).show();
                }
            });

            //set up recycler
            initFriendRecycler();

            //when a friend is selected, store their UID in preferences for the QuipWidget.java class to use
            friendAdapter = new SecureFriendAdapter(friendList, position -> {

                mFriendUID = friendList.get(position);
                final Context context = QuipWidgetConfigureActivity.this;
                // When the button is clicked, store the string locally
                Log.i(TAG, "Row Clicked, storing " + mFriendUID + " in sharedPrefs");
                saveFriendUIDPref(context, mAppWidgetId, mFriendUID);
                // It is the responsibility of the configuration activity to update the app widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                QuipWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                QuipWidgetConfigureActivity.this.setResult(RESULT_OK, resultValue);
                QuipWidgetConfigureActivity.this.finish();
            }, QuipWidgetConfigureActivity.this);


            friendRecycler.setAdapter(friendAdapter);

        }
    }

    private void initFriendRecycler() {
        friendRecycler = findViewById(R.id.quip_widget_configure_recycler);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}