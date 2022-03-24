package com.malikendsley.firebaseutils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.QuipRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.QuipUploadListener;
import com.malikendsley.firebaseutils.interfaces.UsernameResolveListener;
import com.malikendsley.firebaseutils.schema.Friendship;
import com.malikendsley.firebaseutils.schema.Quip;
import com.malikendsley.firebaseutils.schema.SharedQuip;

import java.util.ArrayList;
import java.util.UUID;

public class FirebaseHandler {

    //TODO: augment db interface with caching logic (or leverage firebase's)
    private static final String TAG = "Own";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public FirebaseHandler(DatabaseReference ref) {
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

    //Create a quip in the storage database and if successful, share it
    public void shareQuip(String recipientUID, byte[] byteArray, QuipUploadListener listener) {
        //upload the image to the database
        String path = "users/" + mAuth.getUid() + "/quips/" + UUID.randomUUID() + ".jpeg";
        Log.i(TAG, "Submitting image to " + path);
        StorageReference imageRef = storageRef.child(path);
        UploadTask uploadTask = imageRef.putBytes(byteArray);
        //on fail, notify and on complete, continue with DB op
        uploadTask.addOnFailureListener(listener::onUploadFail).addOnSuccessListener(taskSnapshot -> {
            //the storage database now contains a quip, so add a realtime entry and a shared entry
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                //asynchronously retrieve the URL of the image
                Log.i(TAG, "URI: " + uri);
                Quip q = new Quip(uri.toString(), mAuth.getUid(), (new java.sql.Timestamp(System.currentTimeMillis()).toString()));
                DatabaseReference dbr = mDatabase.child("Quips").push();
                String key = dbr.getKey();
                dbr.setValue(q).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.i(TAG, "Quip Fail");
                        listener.onUploadFail(task.getException());
                    } else {
                        SharedQuip sq = new SharedQuip(uri.toString(), mAuth.getUid(), recipientUID);
                        sq.QID = key;
                        mDatabase.child("SharedQuips").push().setValue(sq).addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Log.i(TAG, "SharedQuip Fail");
                                listener.onUploadFail(task1.getException());
                            } else {
                                Log.i(TAG, "Quip Shared to: " + recipientUID);
                                listener.onUploadComplete(uri.toString());
                            }
                        });
                    }
                });
            });
        });
    }

    public void retrieveMyQuips(QuipRetrieveListener listener) {
        ArrayList<SharedQuip> list = new ArrayList<>();

        mDatabase.child("SharedQuips").orderByChild("Recipient").equalTo(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                listener.onRetrieveFail(task.getException());
            } else {
                for (DataSnapshot sharedQuip : task.getResult().getChildren()) {
                    //retrieve all sharedQuips
                    SharedQuip sq = sharedQuip.getValue(SharedQuip.class);
                    list.add(sq);
                }
                listener.onRetrieveComplete(list);
            }
        });
    }

    public void retrieveSentQuips(QuipRetrieveListener listener) {
        ArrayList<SharedQuip> list = new ArrayList<>();

        mDatabase.child("SharedQuips").orderByChild("Sender").equalTo(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                listener.onRetrieveFail(task.getException());
            } else {
                for (DataSnapshot sharedQuip : task.getResult().getChildren()) {

                    SharedQuip sq = sharedQuip.getValue(SharedQuip.class);
                    list.add(sq);
                }
                listener.onRetrieveComplete(list);
            }
        });
    }
}
