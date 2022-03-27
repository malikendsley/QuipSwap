package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.adapters.RequestAdapter;
import com.malikendsley.firebaseutils.interfaces.RequestClickListener;
import com.malikendsley.firebaseutils.interfaces.RequestRetrieveListener;
import com.malikendsley.firebaseutils.schema.FriendRequest;
import com.malikendsley.firebaseutils.schema.Friendship;

import java.util.ArrayList;

public class FriendRequestsActivity extends AppCompatActivity {

    TextView noFriendRequestsFlavor;
    //recycler
    RecyclerView requestRecycler;
    RequestAdapter requestAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //firebase setup
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<FriendRequest> requestList = new ArrayList<>();
    FirebaseHandler mdb = new FirebaseHandler(mDatabase);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        //retrieve friend requests and populate
        mdb.retrieveReceivedRequests(new RequestRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRequestsRetrieved(ArrayList<FriendRequest> requests) {
                if (requests.isEmpty()) {
                    noFriendRequestsFlavor.setVisibility(View.VISIBLE);
                } else {
                    noFriendRequestsFlavor.setVisibility(View.GONE);
                    requestList.clear();
                    requestList.addAll(requests);
                    requestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onRequestsFailed(Exception e) {
                e.printStackTrace();
                noFriendRequestsFlavor.setVisibility(View.VISIBLE);
                //Log.("Own", "Request Retrieve Failed");
                Toast.makeText(FriendRequestsActivity.this, "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });

        //element setup
        findViewById(R.id.friendRequestsBackButton).setOnClickListener(view -> finish());
        noFriendRequestsFlavor = findViewById(R.id.noPendingRequestsFlavor);

        //friend request recycler setup
        requestRecycler = findViewById(R.id.requestList);
        requestRecycler.setHasFixedSize(true);
        requestRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestAdapter = new RequestAdapter(requestList, new RequestClickListener() {
            @Override
            public void onAcceptClicked(int position) {
                acceptFriend(position);
            }

            @Override
            public void onDenyClicked(int position) {
                denyFriend(position);
            }
        });
        requestRecycler.setAdapter(requestAdapter);

        //change menu icon to filled in bell
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem settingsItem = menu.findItem(R.id.friendRequestsAppBar);
        // set your desired icon here based on a flag if you like
        Drawable whiteBell = AppCompatResources.getDrawable(this, R.drawable.ic_baseline_notifications_24);
        Drawable wrappedDrawable = DrawableCompat.wrap(whiteBell);
        DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
        settingsItem.setIcon(whiteBell);

        return super.onPrepareOptionsMenu(menu);
    }

    //add options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    //handle menu select
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.friendRequestsAppBar:
                finish();
                break;
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

    void acceptFriend(int position) {
        //delete friend request
        deleteFriend(position);
        //add the friend
        Friendship f = new Friendship(mAuth.getUid(), requestList.get(position).getSender(), (new java.sql.Timestamp(System.currentTimeMillis()).toString()));
        mDatabase.child("Friendships").push().setValue(f).addOnSuccessListener(unused -> Toast.makeText(this, "Friend Added", Toast.LENGTH_SHORT).show());
    }

    void denyFriend(int position) {
        deleteFriend(position);
        //Log.i(TAG, "Request Denied");
        Toast.makeText(this, "Request Denied", Toast.LENGTH_SHORT).show();
    }

    void deleteFriend(int position) {
        String key = requestList.get(position).getKey();
        //Log.i(TAG, "Delete request from " + key);
        mDatabase.child("FriendRequests").child(key).removeValue().addOnSuccessListener(deleteRequest -> {
            requestList.remove(position);
            requestAdapter.notifyItemRemoved(position);
        });
    }

}
