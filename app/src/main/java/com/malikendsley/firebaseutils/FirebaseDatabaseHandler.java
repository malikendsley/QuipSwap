package com.malikendsley.firebaseutils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FirebaseDatabaseHandler {

    private static final String TAG = "Own";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;
    WeakReference<FriendRetrieveListener> listenerRef;

    public FirebaseDatabaseHandler(DatabaseReference ref) {
        mDatabase = ref;
    }

    public void retrieveFriends(FriendRetrieveListener listener) {
        listenerRef = new WeakReference<>(listener);

        ArrayList<Friendship> friendList = new ArrayList<>();

        Log.i(TAG, "Requesting friends");
        //retrieve friends and populate
        mDatabase.child("Friendships").orderByChild("User1").equalTo(mAuth.getUid()).get().addOnSuccessListener(user1snapshot -> {
            for (DataSnapshot child : user1snapshot.getChildren()) {
                /*TODO save things like this to disk to minimize reads*/
                friendList.add(child.getValue(Friendship.class));
                Log.i(TAG, "Friend Loaded");
            }
            Log.i(TAG, "User1 loaded");
            mDatabase.child("Friendships").orderByChild("User2").equalTo(mAuth.getUid()).get().addOnSuccessListener(user2snapshot -> {
                for (DataSnapshot child : user2snapshot.getChildren()) {
                    friendList.add(child.getValue(Friendship.class));
                    Log.i(TAG, "Friend Loaded");
                }
                Log.i(TAG, "User2 loaded");

                listenerRef.get().onFriendsRetrieved(friendList);
            });
        });
    }
}
