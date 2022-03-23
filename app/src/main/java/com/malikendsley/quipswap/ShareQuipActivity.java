package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseDatabaseHandler;
import com.malikendsley.firebaseutils.FriendAdapter;
import com.malikendsley.firebaseutils.Friendship;

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
    Bitmap bitmap;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_quip);

        Button cancelButton = findViewById(R.id.shareQuipCancel);

        Log.i(TAG, "Retrieving bitmap");
        //intent work
        Intent intent = getIntent();
        byte[] byteArray = intent.getByteArrayExtra("BitmapImage");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.i(TAG, "Bitmap retrieved");

        //firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mdb = new FirebaseDatabaseHandler(mDatabase);

        //recycler setup
        friendRecycler = findViewById(R.id.selectFriendsRecyclerView);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
        friendAdapter = new FriendAdapter(friendList, position -> debugQuip(friendList.get(position)));
        friendRecycler.setAdapter(friendAdapter);

        mdb.retrieveFriends(friendsList -> {
            Log.i(TAG, "FriendAdapter: Friends Retrieved");
            if (friendsList != null) {
                friendList.clear();
                friendList.addAll(friendsList);
                Log.i(TAG, friendList.toString());
            } else {
                Log.i(TAG, "Retrieved Null");
            }
            friendAdapter.notifyDataSetChanged();
        });


        cancelButton.setOnClickListener(view -> finish());
    }

    void debugQuip(Friendship friendship) {
        Log.i(TAG, "Displaying Dialog");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setIcon(new BitmapDrawable(this.getResources(), bitmap));
        builder.setTitle("Preview Quip");
        builder.setMessage(((friendship.getUser1().equals(mAuth.getUid())) ? friendship.getUser2() : friendship.getUser1()));
        builder.setPositiveButton("Accept", (dialog, i) -> dialog.dismiss());
        builder.setNegativeButton("Deny", (dialog, i) -> dialog.dismiss());
        AlertDialog alert = builder.create();
        alert.show();
    }

    void shareQuip(Friendship recipient) {

        //set up data

        //create quip

        //share quip

        //go back

        Log.i(TAG, "Share quip to " + ((recipient.getUser1().equals(mAuth.getUid())) ? recipient.getUser1() : recipient.getUser2()));
    }
}