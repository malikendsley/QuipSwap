package com.malikendsley.quipswap;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.adapters.FriendAdapter;
import com.malikendsley.firebaseutils.schema.Friendship;
import com.malikendsley.quipswap.databinding.QuipWidgetConfigureBinding;

import java.util.ArrayList;

/**
 * The configuration screen for the {@link QuipWidget QuipWidget} AppWidget.
 */
public class QuipWidgetConfigureActivity extends Activity {

    private static final String TAG = "Own";
    private static final String PREFS_NAME = "com.malikendsley.quipswap.QuipWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    RecyclerView friendRecycler;
    FriendAdapter friendAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<Friendship> friendList = new ArrayList<>();
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    String mFriendUID;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseHandler mdb = new FirebaseHandler(mDatabase);

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
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String friendUID = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (friendUID != null) {
            return friendUID;
        } else {
            return context.getString(R.string.appwidget_text);
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

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        com.malikendsley.quipswap.databinding.QuipWidgetConfigureBinding binding = QuipWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //mAppWidgetText = binding.appwidgetText;
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
            return;
        }

        //populate friends
        //populate friends
        mdb.retrieveFriends(friendsList -> {
            //Log.i(TAG, "Adapter: Friends Retrieved");
            if (friendsList != null) {
                friendList.clear();
                friendList.addAll(friendsList);
                //Log.i(TAG, friendList.toString());
            }
            friendAdapter.notifyDataSetChanged();
        });

        //set up recycler
        initFriendRecycler();

        //when a friend is selected, store their UID in preferences for the QuipWidget.java class to use
        friendAdapter = new FriendAdapter(friendList, position -> {

            mFriendUID = (friendList.get(position).getUser1().equals(mAuth.getUid())) ? friendList.get(position).getUser2() : friendList.get(position).getUser1();
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
        });
        friendRecycler.setAdapter(friendAdapter);


        //mAppWidgetText.setText(loadTitlePref(QuipWidgetConfigureActivity.this, mAppWidgetId));
    }

    private void initFriendRecycler() {
        friendRecycler = findViewById(R.id.quip_widget_configure_recycler);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
    }
}