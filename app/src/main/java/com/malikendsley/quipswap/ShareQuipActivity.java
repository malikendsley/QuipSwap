package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseDatabaseHandler;
import com.malikendsley.firebaseutils.FriendAdapter;
import com.malikendsley.firebaseutils.Friendship;
import com.malikendsley.firebaseutils.interfaces.FriendClickListener;

import java.io.File;
import java.util.ArrayList;

public class ShareQuipActivity extends AppCompatActivity {

    static final String TAG = "Own";
    String quipPath;
    //even in my own projects i can never escape him
    FirebaseDatabaseHandler mdb;

    RecyclerView friendRecycler;
    FriendAdapter friendAdapter;
    DatabaseReference mDatabase;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<Friendship> friendList = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_quip);

        Button cancelButton = findViewById(R.id.shareQuipCancel);

        quipPath = getIntent().getStringExtra("Path");
        Log.i(TAG, "Image path retrieved: " + quipPath);

        //firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mdb = new FirebaseDatabaseHandler(mDatabase);

        //recycler setup
        friendRecycler = findViewById(R.id.selectFriendsRecyclerView);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
        friendAdapter = new FriendAdapter(friendList, new FriendClickListener() {
            @Override
            public void onFriendClicked(int position) {
                shareQuip(friendList.get(position));
            }
        });
        friendRecycler.setAdapter(friendAdapter);

        mdb.retrieveFriends(friendsList -> {
            Log.i(TAG, "Adapter: Friends Retrieved");
            if (friendsList != null) {
                friendList.clear();
                friendList.addAll(friendsList);
                Log.i(TAG, friendList.toString());
            } else {
                Log.i(TAG, "Retrieved Null");
            }
            friendAdapter.notifyDataSetChanged();
        });

        cancelButton.setOnClickListener(view -> {
            File fDelete = new File(quipPath);
            if (fDelete.exists()) {
                if (fDelete.delete()) {
                    Log.i(TAG, "Deleted: " + quipPath);
                    finish();
                } else {
                    Log.i(TAG, "File DNE: " + quipPath);
                }
            } else {
                Log.e(TAG, "Error with file");
            }
        });
    }

    void shareQuip(Friendship recipient) {
        Log.i(TAG, "Share quip to " + ((recipient.getUser1().equals(mAuth.getUid())) ? recipient.getUser2() : recipient.getUser1()));
    }
}