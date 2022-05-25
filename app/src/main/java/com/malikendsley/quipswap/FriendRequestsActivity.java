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
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.GetRequestsListener;
import com.malikendsley.firebaseutils.interfaces.RequestClickListener;
import com.malikendsley.firebaseutils.secureadapters.SecureRequestAdapter;
import com.malikendsley.firebaseutils.secureschema.ExpandableListItem;

import java.util.ArrayList;

public class FriendRequestsActivity extends AppCompatActivity {

    TextView noFriendRequestsFlavor;
    //recycler
    RecyclerView requestRecycler;
    SecureRequestAdapter requestAdapter;
    //firebase setup
    ArrayList<ExpandableListItem> friendRequestList = new ArrayList<>();
    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        //retrieve friend requests and populate
        mdb2.getFriendRequests(new GetRequestsListener() {
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
                    for (String username : requestList) {
                        friendRequestList.add(new ExpandableListItem(username));
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
        });
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
        String user = (String) friendRequestList.get(position).getObject();
        mdb2.acceptFriend(user, () -> deleteFriend(position));
        Toast.makeText(this, "Request Accepted", Toast.LENGTH_SHORT).show();
    }

    void denyFriend(int position) {
        String user = (String) friendRequestList.get(position).getObject();
        mdb2.denyFriend(user);
        Toast.makeText(this, "Request Denied", Toast.LENGTH_SHORT).show();
        deleteFriend(position);
    }

    void deleteFriend(int position) {
        friendRequestList.remove(position);
        requestAdapter.notifyItemRemoved(position);
        noFriendRequestsFlavor.setVisibility(friendRequestList.isEmpty() ? View.VISIBLE : View.GONE);

    }
}
