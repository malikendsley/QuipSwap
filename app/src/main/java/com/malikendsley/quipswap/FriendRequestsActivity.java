package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.ExpandableListItem;
import com.malikendsley.firebaseutils.FirebaseHandler2;
import com.malikendsley.firebaseutils.interfaces.RequestClickListener;
import com.malikendsley.firebaseutils.secureadapters.SecureRequestAdapter;
import com.malikendsley.firebaseutils.secureinterfaces.GetRequestsListener;

import java.util.ArrayList;
import java.util.Objects;

public class FriendRequestsActivity extends AppCompatActivity {

    TextView noFriendRequestsFlavor;
    //recycler
    RecyclerView requestRecycler;
    SecureRequestAdapter requestAdapter;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    //firebase setup
    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    ArrayList<ExpandableListItem> friendRequestList = new ArrayList<>();
    //TODO migrate complete
    FirebaseHandler2 mdb = new FirebaseHandler2(mDatabase, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        //retrieve friend requests and populate
        mdb.getReceivedFriendRequests(new GetRequestsListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRequests(ArrayList<String> requestList) {
                Log.i("Own", "Friends retrieved");
                if (requestList.isEmpty()) {
                    noFriendRequestsFlavor.setVisibility(View.VISIBLE);
                } else {
                    Log.i("Own", "Friends present");
                    noFriendRequestsFlavor.setVisibility(View.GONE);
                    friendRequestList.clear();
                    for (String UID : requestList) {
                        friendRequestList.add(new ExpandableListItem(UID));
                    }
                    requestAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFail(Exception e) {
                e.printStackTrace();
                noFriendRequestsFlavor.setVisibility(View.VISIBLE);
                Log.e("Own", "Request Retrieve Failed");
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

        requestAdapter = new SecureRequestAdapter(friendRequestList, new RequestClickListener() {
            @Override
            public void onAcceptClicked(int position) {
                acceptFriend(position);
            }

            @Override
            public void onDenyClicked(int position) {
                denyFriend(position);
            }
        }, this);
        requestRecycler.setAdapter(requestAdapter);
    }

    //add options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_friend_requests, menu);

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
        //TODO this should probably be a cloud function
        Long time = System.currentTimeMillis();
        mDatabase.child("FriendsPrivate").child(Objects.requireNonNull(mAuth.getUid())).child((String) friendRequestList.get(position).getObject()).setValue(time);
        mDatabase.child("FriendsPrivate").child((String) friendRequestList.get(position).getObject()).child(mAuth.getUid()).setValue(time);
        Toast.makeText(this, "Accepted", Toast.LENGTH_SHORT).show();
        deleteFriend(position);
    }

    void denyFriend(int position) {
        //remove incoming in our list
        mDatabase.child("RequestsPrivate").child(Objects.requireNonNull(mAuth.getUid())).child("Incoming").child((String) friendRequestList.get(position).getObject()).removeValue();
        //remove outgoing in theirs
        mDatabase.child("RequestsPrivate").child((String) friendRequestList.get(position).getObject()).child("Outgoing").child(Objects.requireNonNull(mAuth.getUid())).removeValue();
        Toast.makeText(this, "Request Denied", Toast.LENGTH_SHORT).show();
        deleteFriend(position);
    }

    void deleteFriend(int position) {
        friendRequestList.remove(position);
        requestAdapter.notifyItemRemoved(position);
        noFriendRequestsFlavor.setVisibility(friendRequestList.isEmpty() ? View.VISIBLE : View.GONE);

    }
}
