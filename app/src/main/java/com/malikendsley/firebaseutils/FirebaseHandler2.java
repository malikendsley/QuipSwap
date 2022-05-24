package com.malikendsley.firebaseutils;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.malikendsley.firebaseutils.interfaces.FriendAddListener;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.GetRequestsListener;
import com.malikendsley.firebaseutils.interfaces.PrivateQuipRetrievedListener;
import com.malikendsley.firebaseutils.interfaces.PublicQuipRetrieveListener;
import com.malikendsley.firebaseutils.interfaces.QuipUploadListener;
import com.malikendsley.firebaseutils.interfaces.RecentQuipListener;
import com.malikendsley.firebaseutils.interfaces.RegisterUserListener;
import com.malikendsley.firebaseutils.interfaces.ResolveListener;
import com.malikendsley.firebaseutils.interfaces.UserRetrievedListener;
import com.malikendsley.firebaseutils.interfaces.UsernameResolveListener;
import com.malikendsley.firebaseutils.secureschema.PrivateQuip;
import com.malikendsley.firebaseutils.secureschema.PrivateUser;
import com.malikendsley.firebaseutils.secureschema.PublicQuip;
import com.malikendsley.firebaseutils.secureschema.PublicUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class FirebaseHandler2 {

    //TODO: augment db interface with caching logic (or leverage firebase's)
    private static final String TAG = "FBH2";
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    Activity mActivity;

    public FirebaseHandler2(DatabaseReference ref, Activity activity) {
        mDatabase = ref;
        mActivity = activity;
    }

    public FirebaseHandler2(DatabaseReference ref) {
        mDatabase = ref;
        mActivity = null;
    }

    //convert a UID to a username
    public void UIDtoUsername(String UID, ResolveListener listener) {
        mDatabase.child("UsersPublic").child(UID).child("Username").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String username = String.valueOf(task.getResult().getValue());
                listener.onResolved(username);
                Log.i(TAG, "UIDtoUsername Resolved: " + username);
            } else {
                Log.e(TAG, "UIDtoUsername: failed");
            }
        });
    }

    /*convert a username to a UID
    public void usernameToUID(String username, UsernameResolveListener listener) {
        mDatabase.child("UidLookup").child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String UID = (String) task.getResult().getValue();
                Log.i(TAG, "usernameToUID Resolved: " + UID);
                if (UID != null) {
                    listener.onUsernameResolved(UID);
                } else {
                    listener.onUsernameResolved(null);
                    Log.e(TAG, "UID Null");
                }
            } else {
                listener.onUsernameResolved(null);
                Log.e(TAG, "usernameToUID Failed, effectively null");
            }
        });
    }
    */

    //check if a username is taken
    public void isTaken(String username, UsernameResolveListener listener) {
        mDatabase.child("TakenUsernames").child(username).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String UID = (String) task.getResult().getValue();
                if (UID != null) {
                    Log.i(TAG, "usernameToUID Resolved: " + UID);
                    listener.onUsernameResolved("taken");
                } else {
                    listener.onUsernameResolved(null);
                    Log.e(TAG, "UID Null");
                }
            } else {
                listener.onUsernameResolved(null);
                Log.e(TAG, "isTaken Failed, effectively null");
            }
        });
    }

    //get a user
    @SuppressWarnings("unused")
    public void getUser(String UID, UserRetrievedListener listener) {
        mDatabase.child("UsersPrivate").child(UID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.i(TAG, "getUser: succeeded");
                PrivateUser p = task.getResult().getValue(PrivateUser.class);
                listener.onUserRetrieved(p);
            } else {
                Log.e(TAG, "getUser: failed");
                listener.onRetrieveFailed(task.getException());
            }
        });
    }


    public void getQuipByKey(String quipKey, PrivateQuipRetrievedListener listener) {
        mDatabase.child("QuipsPrivate").child(quipKey).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() || task.getResult().exists()) {
                listener.onRetrieveComplete(task.getResult().getValue(PrivateQuip.class));
            } else {
                Log.e(TAG, "getQuipByKey: Read Failed");
                listener.onRetrieveFail(task.getException());
            }
        });
    }

    //retrieve friends
    public void getFriends(FriendRetrieveListener listener) {
        UIDtoUsername(mAuth.getUid(), resolved -> mDatabase.child("FriendsPrivate").child(resolved).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> l = new ArrayList<>();
                for (DataSnapshot child : task.getResult().getChildren()) {
                    Log.i(TAG, "getFriends Retrieved: " + child.getKey());
                    l.add(child.getKey());
                }
                Log.i(TAG, l.toString());
                listener.onGetFriends(l);
            } else {
                Log.e(TAG, "getFriends: failed");
                listener.onGetFailed(task.getException());
            }
        }));
    }

    //share a quip to a user
    public void shareQuip(String recipientUID, byte[] byteArray, QuipUploadListener listener) {
        //upload the image to the database
        String path = "users/" + mAuth.getUid() + "/quips/" + UUID.randomUUID() + ".jpeg";
        Log.i(TAG, "shareQuip: Path: " + path);
        StorageReference imageRef = storageRef.child(path);
        UploadTask uploadTask = imageRef.putBytes(byteArray);
        //on fail notify, on success, write to database
        uploadTask.addOnFailureListener(listener::onUploadFail).addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            //log the URI in the appropriate section
            long time = System.currentTimeMillis();
            PrivateQuip privateQuip = new PrivateQuip(mAuth.getUid(), recipientUID, time, uri.toString());
            PublicQuip publicQuip = new PublicQuip(mAuth.getUid(), recipientUID, time);

            //these can be set up at the same time, they would fail or succeed for the same reason
            DatabaseReference publicQuipsReference = mDatabase.child("QuipsPublic").push();
            privateQuip.setKey(publicQuipsReference.getKey());
            publicQuip.setKey(publicQuipsReference.getKey());
            publicQuipsReference.setValue(publicQuip).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    mDatabase.child("QuipsPrivate").child(Objects.requireNonNull(publicQuipsReference.getKey())).setValue(privateQuip).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            //all clear
                            Log.i(TAG, "shareQuip: Success - " + uri);
                            listener.onUploadComplete(uri.toString());
                        } else {
                            Log.e(TAG, "shareQuip: private fail");
                            listener.onUploadFail(task1.getException());
                        }
                    });
                } else {
                    Log.e(TAG, "shareQuip: public fail");
                    listener.onUploadFail(task.getException());
                }
            });
        })).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.i(TAG, "Handler: Upload is " + progress + "% done");
            listener.onProgress(progress);
        });
    }

    //retrieve quips received
    public void getReceivedQuips(PublicQuipRetrieveListener listener) {
        ArrayList<PublicQuip> l = new ArrayList<>();
        mDatabase.child("QuipsPublic").orderByChild("Recipient").equalTo(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                listener.onRetrieveFail(task.getException());
            } else {
                for (DataSnapshot child : task.getResult().getChildren()) {
                    Log.i(TAG, "getReceivedQuips: key = " + child.getKey());
                    l.add(child.getValue(PublicQuip.class));
                }
                listener.onRetrieveComplete(l);
            }
        });
    }

    //retrieve quips sent
    public void getSentQuips(PublicQuipRetrieveListener listener) {
        ArrayList<PublicQuip> l = new ArrayList<>();
        mDatabase.child("QuipsPublic").orderByChild("Sender").equalTo(mAuth.getUid()).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                listener.onRetrieveFail(task.getException());
            } else {
                for (DataSnapshot child : task.getResult().getChildren()) {
                    Log.i(TAG, "getReceivedQuips: key = " + child.getKey());
                    l.add(child.getValue(PublicQuip.class));
                }
                listener.onRetrieveComplete(l);
            }
        });
    }

    //retrieve incoming friend requests
    //TODO Refactor
    public void getReceivedFriendRequests(GetRequestsListener listener) {
        mDatabase.child("RequestsPrivate").child(Objects.requireNonNull(mAuth.getUid())).child("Incoming").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> l = new ArrayList<>();
                for (DataSnapshot child : task.getResult().getChildren()) {
                    Log.i(TAG, "FriendUID Retrieved: " + child.getKey());
                    l.add(child.getKey());
                }
                listener.onRequests(l);
            } else {
                Log.e(TAG, "getReceivedFriendRequests: Failed");
                listener.onGetFail(task.getException());
            }
        });
    }

    //try to add a user as a friend
    //Requires a string of usernames
    public void trySendFriendRequest(ArrayList<String> friendsList, String username, FriendAddListener listener) {
        //retrieve own username
        UIDtoUsername(mAuth.getUid(), ownUsername -> {
            if (ownUsername.equals(username)) {
                //prevent self-add
                Log.e(TAG, "trySendFriendRequest: Self Add detected");
                listener.onResult("You cannot add yourself");
            } else {
                //check if user exists
                isTaken(username, taken -> {
                    if (taken == null) {
                        //only allow adding users that exist
                        Log.e(TAG, "trySendFriendRequest: User DNE");
                        listener.onResult("User does not exist");
                    } else if (friendsList.contains(username)) {
                        Log.e(TAG, "trySendFriendRequest: Friend already exists");
                        listener.onResult("Already friends with this user");
                    } else {
                        mDatabase.child("FriendRequests").child(username).child("Outgoing").child(ownUsername).get().addOnCompleteListener(outgoingTask -> {
                            if (outgoingTask.isSuccessful()) {
                                //prevent duplicate requests
                                if (outgoingTask.getResult().getValue() != null) {
                                    Log.e(TAG, "trySendFriendRequest: Already sent");
                                    listener.onResult("Request already sent");
                                } else {
                                    mDatabase.child("FriendRequests").child(ownUsername).child("Outgoing").child(username).get().addOnCompleteListener(incomingTask -> {
                                        if (incomingTask.isSuccessful()) {
                                            //prevent cross-send
                                            if (incomingTask.getResult().getValue() != null) {
                                                Log.e(TAG, "trySendFriendRequest: Already incoming");
                                                listener.onResult("Accept the incoming request instead");
                                            } else {
                                                //all clear
                                                Log.i(TAG, "trySendFriendRequest: Sending Request");//mark outgoing in our list
                                                mDatabase.child("FriendRequests").child(ownUsername).child("Outgoing").child(username).setValue(true);//mark incoming in theirs
                                                mDatabase.child("FriendRequests").child(username).child("Incoming").child(ownUsername).setValue(true);//mark outgoing in ours
                                                listener.onResult("");
                                            }
                                        } else {
                                            listener.onDatabaseException(incomingTask.getException());
                                            Log.e(TAG, "trySendFriendRequest: incoming requests check failed");
                                        }
                                    });
                                }
                            } else {
                                listener.onDatabaseException(outgoingTask.getException());
                                Log.e(TAG, "trySendFriendRequest: outgoing requests check failed");
                            }
                        });
                    }

                });
            }
        });
    }

    //TODO the scaling is not fixed at this point, still try to implement metadata
    //get most recent quip from user as a bitmap
    public void getLatestQuip(String UID, RecentQuipListener listener) {
        Log.i(TAG, "Latest Quip Called");
        //get all quips sent to you
        getReceivedQuips(new PublicQuipRetrieveListener() {
            @Override
            public void onRetrieveComplete(ArrayList<PublicQuip> quipList) {
                if (quipList.isEmpty()) {
                    Log.i(TAG, "getLatestQuip: Quip List is empty");
                    listener.onRetrieved(null);
                    return;
                }
                //filter for quips from a particular user
                ArrayList<PublicQuip> toRemove = new ArrayList<>();
                for (PublicQuip quip : quipList) {
                    if (!quip.getSender().equals(UID)) {
                        toRemove.add(quip);
                    }
                }
                if (!toRemove.isEmpty()) {
                    quipList.removeAll(toRemove);
                }
                //quips implement comparable by timestamp
                PublicQuip mostRecent = Collections.max(quipList);
                //TODO: since this is likely to change, don't make a function for it
                mDatabase.child("QuipsPrivate").child(mostRecent.getKey()).get().addOnSuccessListener(dataSnapshot -> {

                    String URI = Objects.requireNonNull(dataSnapshot.getValue(PrivateQuip.class)).getURI();
                    StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(URI);
                    //just in case someone managed to pull something
                    final long ONE_MEGABYTE = 1024 * 1024;
                    Log.i(TAG, "Returning bitmap");
                    httpsReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> listener.onRetrieved(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))).addOnFailureListener(e -> {
                        Log.i(TAG, "getLatestQuip: URL Download Failed");
                        e.printStackTrace();
                    });
                });
            }

            @Override
            public void onRetrieveFail(Exception e) {
                listener.onFailed(e);
            }
        });
    }

    //register a user
    public void registerUser(String username, String email, String password, RegisterUserListener listener) {
        if (mActivity == null) {
            Log.e(TAG, "registerUser: Called with null activity");
            return;
        }
        //prevent duplicate usernames
        isTaken(username, UID -> {
            if (UID == null) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mActivity, task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "registerUser: User Registered");
                        PublicUser publicUser = new PublicUser(username);
                        PrivateUser privateUser = new PrivateUser(email, username);
                        //set up destinations
                        mDatabase.child("UsersPublic").child(Objects.requireNonNull(mAuth.getUid())).setValue(publicUser).addOnCompleteListener(publicTask -> {
                            if (publicTask.isSuccessful()) {
                                mDatabase.child("UsersPrivate").child(mAuth.getUid()).setValue(privateUser).addOnCompleteListener(privateTask -> {
                                    if (privateTask.isSuccessful()) {
                                        mDatabase.child("UidLookup").child(username).setValue(mAuth.getUid()).addOnCompleteListener(indexTask -> {
                                            if (indexTask.isSuccessful()) {
                                                mDatabase.child("TakenUsernames").child(username).setValue(true).addOnCompleteListener(takenTask -> {
                                                    if (takenTask.isSuccessful()) {
                                                        Log.i(TAG, "registerUser: success");
                                                        listener.onResult("");
                                                    } else {
                                                        //first index failed
                                                        Log.e(TAG, "registerUser: UID lookup index failed");
                                                        listener.onDBFail(takenTask.getException());
                                                    }
                                                });
                                            } else {
                                                //second index failed
                                                Log.e(TAG, "registerUser: UID lookup index failed");
                                                listener.onDBFail(indexTask.getException());
                                            }
                                        });
                                    } else {
                                        //private record failed
                                        Log.e(TAG, "registerUser: private write failed");
                                        listener.onDBFail(privateTask.getException());
                                    }
                                });
                            } else {
                                //public record failed
                                Log.e(TAG, "registerUser: public write failed");
                                listener.onDBFail(publicTask.getException());
                            }
                        });
                    } else {
                        //create user failed
                        Log.i(TAG, "registerUser: User not registered");
                        listener.onDBFail(task.getException());
                    }
                });
            } else {
                //username taken
                Log.e(TAG, "registerUser: Username Taken");
                listener.onResult("Username already taken");
            }
        });
    }
}
