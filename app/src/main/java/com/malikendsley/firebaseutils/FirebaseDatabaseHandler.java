package com.malikendsley.firebaseutils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.UsernameResolveListener;

import java.util.ArrayList;

public class FirebaseDatabaseHandler {

    //TODO: augment db interface with caching logic (or leverage firebase's)
    private static final String TAG = "Own";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;

    public FirebaseDatabaseHandler(DatabaseReference ref) {
        mDatabase = ref;
    }

    //retrieve a list of a User's friends
    public void retrieveFriends(FriendRetrieveListener listener) {
        ArrayList<Friendship> friendList = new ArrayList<>();

        Log.i(TAG, "Retrieving friends");
        //retrieve friends and populate
        mDatabase.child("Friendships").orderByChild("User1").equalTo(mAuth.getUid()).get().addOnSuccessListener(user1snapshot -> {
            for (DataSnapshot child : user1snapshot.getChildren()) {
                friendList.add(child.getValue(Friendship.class));
                //Log.i(TAG, "Friend Loaded");
            }
            //Log.i(TAG, "User1 loaded");
            mDatabase.child("Friendships").orderByChild("User2").equalTo(mAuth.getUid()).get().addOnSuccessListener(user2snapshot -> {
                for (DataSnapshot child : user2snapshot.getChildren()) {
                    friendList.add(child.getValue(Friendship.class));
                    //Log.i(TAG, "Friend Loaded");
                }
                //Log.i(TAG, "User2 loaded");

                listener.onFriendsRetrieved(friendList);
            });
        });
    }

    //Resolve a username to a UID
    public void resolveUsername(String username, UsernameResolveListener listener) {
        mDatabase.child("TakenUsernames").child(username).get().addOnCompleteListener(task -> {
            String UID = (String) task.getResult().getValue();
            Log.i(TAG, "UID: " + UID);
            if (UID != null) {
                listener.onUsernameResolved(UID);
            } else {
                listener.onUsernameResolved(null);
                Log.e(TAG, "UID Null");
            }
        });
    }

    //Create a Quip in the database
//    public void createQuip(String )
    //Share a Quip to one user

    //Share a Quip to the following users

}
