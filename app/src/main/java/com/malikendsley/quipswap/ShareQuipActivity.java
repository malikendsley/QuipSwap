package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler2;
import com.malikendsley.firebaseutils.interfaces.QuipUploadListener;
import com.malikendsley.firebaseutils.secureadapters.SecureFriendAdapter;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;

import java.util.ArrayList;

public class ShareQuipActivity extends AppCompatActivity {

    static final String TAG = "Own";
    //even in my own projects i can never escape him
    FirebaseHandler2 mdb2;

    RecyclerView friendRecycler;
    SecureFriendAdapter friendAdapter;
    DatabaseReference mDatabase;

    LinearProgressIndicator progressIndicator;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    ArrayList<String> friendList = new ArrayList<>();
    byte[] byteArray;
    Bitmap bitmap;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_quip);

        Button cancelButton = findViewById(R.id.shareQuipCancel);
        progressIndicator = findViewById(R.id.quip_upload_progress_bar);

        Log.i(TAG, "Retrieving bitmap");
        //intent work
        Intent intent = getIntent();
        byteArray = intent.getByteArrayExtra("BitmapImage");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.i(TAG, "Bitmap retrieved");
        //firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mdb2 = new FirebaseHandler2(mDatabase, this);

        //recycler setup
        friendRecycler = findViewById(R.id.selectFriendsRecyclerView);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
        friendAdapter = new SecureFriendAdapter(friendList, position -> {
            //show progress bar
            progressIndicator.setVisibility(View.VISIBLE);
            ShareQuipActivity.this.shareQuip(friendList.get(position));
        }, this);
        friendRecycler.setAdapter(friendAdapter);

        //populate friends
        mdb2.getFriends(new FriendRetrieveListener() {
            @Override
            public void onGetFriends(ArrayList<String> friendUIDList) {
                if (friendUIDList != null) {
                    friendList.clear();
                    friendList.addAll(friendUIDList);
                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(ShareQuipActivity.this, "Trouble retrieving friends", Toast.LENGTH_SHORT).show();
            }
        });


        cancelButton.setOnClickListener(view -> finish());
    }

    void shareQuip(String recipientUID) {
        Log.e(TAG, "ShareQuip: " + recipientUID);
        mdb2.shareQuip(recipientUID, byteArray, new QuipUploadListener() {
            @Override
            public void onUploadComplete(String URI) {
                Toast.makeText(ShareQuipActivity.this, "Quip Shared!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ShareQuipActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                ShareQuipActivity.this.finish();
                //hide progress bar
                progressIndicator.setVisibility(View.GONE);

            }

            @Override
            public void onUploadFail(Exception e) {
                Toast.makeText(ShareQuipActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
                e.printStackTrace();

            }

            @Override
            public void onProgress(double progress) {
                //update progress
                progressIndicator.setProgressCompat((int) progress, true);
            }
        });
    }
}