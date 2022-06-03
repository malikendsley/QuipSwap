package com.malikendsley.quipswap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.utils.FirebaseHandler;
import com.malikendsley.utils.adapters.FriendAdapter;
import com.malikendsley.utils.interfaces.FriendRetrieveListener;
import com.malikendsley.utils.interfaces.QuipUploadListener;

import java.util.ArrayList;

public class ShareQuipActivity extends AppCompatActivity {

    //static final String TAG = "Own";
    //even in my own projects i can never escape him
    FirebaseHandler mdb2;

    RecyclerView friendRecycler;
    FriendAdapter friendAdapter;

    LinearProgressIndicator progressIndicator;

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

        //Log.i(TAG, "Retrieving bitmap");
        //intent work
        Intent intent = getIntent();
        byteArray = intent.getByteArrayExtra("BitmapImage");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        //Log.i(TAG, "Bitmap retrieved");
        //firebase setup
        mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), this);

        //recycler setup
        friendRecycler = findViewById(R.id.selectFriendsRecyclerView);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(this));
        friendAdapter = new FriendAdapter(friendList, position -> {
            //show progress bar
            progressIndicator.setVisibility(View.VISIBLE);
            shareQuip(friendList.get(position));
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
                //e.printStackTrace();
                Toast.makeText(ShareQuipActivity.this, "Trouble retrieving friends", Toast.LENGTH_SHORT).show();
            }
        });


        cancelButton.setOnClickListener(view -> finish());
    }

    void shareQuip(String recipientUsername) {
        mdb2.usernameToUID(recipientUsername, resolved -> {
            //Log.e(TAG, "ShareQuip: " + resolved);
            mdb2.shareQuip(resolved, byteArray, new QuipUploadListener() {
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
                    //Log.e(TAG, e.toString());
                    //e.printStackTrace();

                }

                @Override
                public void onProgress(double progress) {
                    //update progress
                    progressIndicator.setProgressCompat((int) progress, true);
                }
            });
        });
    }
}