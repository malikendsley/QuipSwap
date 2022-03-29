package com.malikendsley.firebaseutils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikendsley.firebaseutils.interfaces.FriendAddListener;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.QuipRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.QuipUploadListener;
import com.malikendsley.firebaseutils.interfaces.RequestRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.UserRetrievedListener;
import com.malikendsley.firebaseutils.interfaces.UsernameResolveListener;
import com.malikendsley.firebaseutils.schema.FriendRequest;
import com.malikendsley.firebaseutils.schema.Friendship;
import com.malikendsley.firebaseutils.schema.Quip;
import com.malikendsley.firebaseutils.schema.SharedQuip;
import com.malikendsley.firebaseutils.schema.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Objects;
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
                Long time = System.currentTimeMillis();
                Quip q = new Quip(uri.toString(), mAuth.getUid(), time.toString());
                DatabaseReference dbr = mDatabase.child("Quips").push();
                String key = dbr.getKey();
                dbr.setValue(q).addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.i(TAG, "Quip Fail");
                        listener.onUploadFail(task.getException());
                    } else {
                        SharedQuip sq = new SharedQuip(uri.toString(), mAuth.getUid(), recipientUID, time.toString());
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

    public void retrieveReceivedQuips(QuipRetrieveListener listener) {
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

    public void retrieveReceivedRequests(RequestRetrieveListener listener) {
        ArrayList<FriendRequest> list = new ArrayList<>();

        //retrieve friend requests and populate
        mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(mAuth.getUid()).get().addOnSuccessListener(requestSnapshot -> {
            for (DataSnapshot child : requestSnapshot.getChildren()) {
                FriendRequest fr = child.getValue(FriendRequest.class);
                Objects.requireNonNull(fr).setKey(child.getKey());
                list.add(fr);
            }
            listener.onRequestsRetrieved(list);
        }).addOnFailureListener(listener::onRequestsFailed);

    }

    public void retrieveSentRequests(RequestRetrieveListener listener) {
        ArrayList<FriendRequest> list = new ArrayList<>();

        //retrieve friend requests
        mDatabase.child("FriendRequests").orderByChild("Sender").equalTo(mAuth.getUid()).get().addOnSuccessListener(dataSnapshot -> {
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                FriendRequest fr = child.getValue(FriendRequest.class);
                Objects.requireNonNull(fr).setKey(child.getKey());
                list.add(fr);
            }
            listener.onRequestsRetrieved(list);
        }).addOnFailureListener(listener::onRequestsFailed);
    }

    public void retrieveUser(String UID, UserRetrievedListener listener) {
        mDatabase.child("Users").child(UID).get().addOnSuccessListener(dataSnapshot -> {
            User user = (dataSnapshot.getValue(User.class));
            if (user != null) {
                Log.i(TAG, user.toString());
                listener.onUserRetrieved(user);
            } else {
                Log.i(TAG, "retrieveUser problem");
            }
        }).addOnFailureListener(listener::onRetrieveFailed);
    }

    public void tryAddFriend(String friendUID, FriendAddListener listener) {
        //validate, try to add friend according to rules
        resolveUsername(friendUID, resolvedUID -> {
            //must exist
            if (resolvedUID == null) {
                listener.onResult("User does not exist");
                return;
            }
            //prevent self add
            if (resolvedUID.equals(mAuth.getUid())) {
                //Log.i(TAG, "Self-add detected");
                listener.onResult("You cannot add yourself");
                return;
            }
            //prevent add if already outgoing
            retrieveSentRequests(new RequestRetrieveListener() {
                @Override
                public void onRequestsRetrieved(ArrayList<FriendRequest> sentRequests) {
                    for (FriendRequest request : sentRequests) {
                        if (request.getRecipient().equals(resolvedUID)) {
                            listener.onResult("Already sent request");
                            return;
                        }
                    }
                    //prevent if already incoming
                    retrieveReceivedRequests(new RequestRetrieveListener() {
                        @Override
                        public void onRequestsRetrieved(ArrayList<FriendRequest> retrievedRequests) {
                            for (FriendRequest request : retrievedRequests) {
                                if (request.getSender().equals(resolvedUID)) {
                                    listener.onResult("Accept the pending request instead");
                                    return;
                                }
                            }
                            retrieveFriends(friendsList -> {
                                for (Friendship friend : friendsList) {
                                    if (friend.getUser1().equals(resolvedUID) || friend.getUser2().equals(resolvedUID)) {
                                        //Log.i(TAG, "Already friends");
                                        listener.onResult("Already friends with this user");
                                        return;
                                    }
                                }
                                //all clear
                                //Log.i(TAG, "Request created");
                                mDatabase.child("FriendRequests").push().setValue(new FriendRequest(mAuth.getUid(), friendUID));
                                listener.onResult("");
                            });
                        }

                        @Override
                        public void onRequestsFailed(Exception e) {
                            listener.onDatabaseException(e);
                        }
                    });
                }

                @Override
                public void onRequestsFailed(Exception e) {
                    listener.onDatabaseException(e);
                }
            });
        });
    }
}
