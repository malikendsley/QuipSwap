package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseDatabaseHandler;
import com.malikendsley.firebaseutils.FriendAdapter;
import com.malikendsley.firebaseutils.FriendRequest;
import com.malikendsley.firebaseutils.Friendship;
import com.malikendsley.firebaseutils.RequestAdapter;
import com.malikendsley.firebaseutils.RequestClickListener;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;
import java.util.Objects;

public class FriendsFragment extends Fragment {
    private static final String TAG = "Own";

    FirebaseDatabaseHandler mdb;

    RecyclerView friendRecycler;
    FriendAdapter friendAdapter;

    RecyclerView requestRecycler;
    RequestAdapter requestAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference mDatabase;
    ArrayList<Friendship> friendList = new ArrayList<>();
    ArrayList<FriendRequest> requestList = new ArrayList<>();


    boolean dataFetched = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends, container, false);

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //friend recycler setup
        friendRecycler = requireActivity().findViewById(R.id.friendList);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new FriendAdapter(friendList, position -> {

        });
        friendRecycler.setAdapter(friendAdapter);

        //friend request recycler setup
        requestRecycler = requireActivity().findViewById(R.id.requestList);
        requestRecycler.setHasFixedSize(true);
        requestRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //safe to use requestList, loading it is a prerequisite to accessing these buttons
        requestAdapter = new RequestAdapter(requestList, new RequestClickListener() {
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

        Button addFriendButton = requireActivity().findViewById(R.id.addFriendButton);
        EditText friendSearch = requireActivity().findViewById(R.id.friendSearchUsername);

        friendSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                Log.i(TAG, "Add friend clicked via soft keyboard");
                tryAddFriend(friendSearch.getText().toString());
                return true;
            } else {
                return false;
            }
        });

        addFriendButton.setOnClickListener(view1 -> {
            Log.i(TAG, "Add friend clicked");
            //create new friend request
            validateFriendUsername(friendSearch.getText().toString());
        });

        mdb = new FirebaseDatabaseHandler(mDatabase);
        mdb.retrieveFriends(friendsList -> {
            Log.i(TAG, "Adapter: Friends Retrieved");
            if (friendsList != null) {
                friendList.clear();
                friendList.addAll(friendsList);
                dataFetched = true;
                Log.i(TAG, friendList.toString());
            } else {
                Log.i(TAG, "Retrieved Null");
            }
            friendAdapter.notifyDataSetChanged();
        });

        //retrieve friend requests and populate
        mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(mAuth.getUid()).get().addOnSuccessListener(requestSnapshot -> {
            for (DataSnapshot child : requestSnapshot.getChildren()) {
                FriendRequest fr = child.getValue(FriendRequest.class);
                Objects.requireNonNull(fr).setKey(child.getKey());
                requestList.add(fr);
            }
            requestAdapter.notifyDataSetChanged();
        });
    }

    private void validateFriendUsername(String username) {
        if (username == null || username.equals("")) {
            Log.i(TAG, "Empty Add Friend");
            Toast.makeText(getContext(), "Please specify a user", Toast.LENGTH_SHORT).show();
        } else {
            tryAddFriend(username);
        }
    }

    //TODO: replace username lookup with SharedPreferences Query (which will need populating)

    void tryAddFriend(String username) {
        //user must exist
        //you cannot send 2 requests to the same person
        //you cannot request a person who has requested you already
        //you can't send a request to your friend or yourself

        //it is very unlikely that a user manages to add a friend before their friends list loads
        //but this code handles that
        Log.i(TAG, "Check load friends");
        if (!dataFetched) {
            Log.i(TAG, "Friends not loaded");
            Toast.makeText(getContext(), "Please wait a moment then try again", Toast.LENGTH_SHORT).show();
            return;
        }

        //first retrieve UID of username
        mDatabase.child("TakenUsernames").child(username).get().addOnSuccessListener(dataSnapshot -> {
            //user must exist
            if (dataSnapshot.getValue() == null) {
                Log.i(TAG, "FriendsFragment: No such user");
                Toast.makeText(getContext(), "This user does not exist", Toast.LENGTH_SHORT).show();
                return;
            }
            String addUID = dataSnapshot.getValue().toString();
            Log.i(TAG, "addUID = " + addUID);
            //prevent self add
            if (addUID.equals(mAuth.getUid())) {
                Log.i(TAG, "Self-add detected");
                Toast.makeText(getContext(), "You can't add yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            //prevent adding if incoming request exists
            for (FriendRequest request : requestList) {
                if (request.getSender().equals(addUID)) {
                    Log.i(TAG, "Cross-send attempt");
                    Toast.makeText(getContext(), "Accept the pending request instead", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            //prevent adding if outgoing request exists
            mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(addUID).get().addOnSuccessListener(matchingIncoming -> {
                if (matchingIncoming.exists()) {
                    for (DataSnapshot fr : matchingIncoming.getChildren()) {
                        FriendRequest mfr = fr.getValue(FriendRequest.class);
                        if (Objects.requireNonNull(mfr).getSender().equals(mAuth.getUid())) {
                            Log.i(TAG, "Already outgoing");
                            Toast.makeText(getContext(), "Already sent Request", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                Log.i(TAG, "Loading friends by User 1");
                mDatabase.child("Friendships").orderByChild("User1").equalTo(mAuth.getUid()).get().addOnSuccessListener(user1Snapshot -> {
                    if (user1Snapshot.exists()) {
                        for (DataSnapshot friend : user1Snapshot.getChildren()) {
                            if (Objects.requireNonNull(friend.getValue(Friendship.class)).getUser2().equals(addUID)) {
                                Log.i(TAG, "Already friends");
                                Toast.makeText(getContext(), "Already friends with this user", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                    Log.i(TAG, "Loading friends by User 2");
                    mDatabase.child("Friendships").orderByChild("User2").equalTo(mAuth.getUid()).get().addOnSuccessListener(user2Snapshot -> {
                        if (user2Snapshot.exists()) {
                            for (DataSnapshot friend : user2Snapshot.getChildren()) {
                                if (Objects.requireNonNull(friend.getValue(Friendship.class)).getUser1().equals(addUID)) {
                                    Log.i(TAG, "Already friends");
                                    Toast.makeText(getContext(), "Already friends with this user", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        }

                        //check friend requests
                        //all clear
                        Log.i(TAG, "Request created");
                        createRecord(addUID);
                    });
                });

            });
        });
    }

    //simple helper function to clean things up
    void createRecord(String friendUID) {
        mDatabase.child("FriendRequests").push().setValue(new FriendRequest(mAuth.getUid(), friendUID));
        Log.i(TAG, "Request sent");
        Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
    }

    void acceptFriend(int position) {
        //delete friend request
        deleteFriend(position);
        //add the friend
        Friendship f = new Friendship(mAuth.getUid(), requestList.get(position).getSender(), (new java.sql.Timestamp(System.currentTimeMillis()).toString()));
        mDatabase.child("Friendships").push().setValue(f).addOnSuccessListener(unused -> {
            Log.i(TAG, "Friend added");
            Toast.makeText(getContext(), "Friend Added", Toast.LENGTH_SHORT).show();
            friendList.add(f);
            friendAdapter.notifyItemInserted(friendList.size());
        });
    }

    void denyFriend(int position) {
        deleteFriend(position);
        Log.i(TAG, "Request Denied");
        Toast.makeText(getContext(), "Request Denied", Toast.LENGTH_SHORT).show();
    }

    void deleteFriend(int position) {
        String key = requestList.get(position).getKey();
        Log.i(TAG, "Delete request from " + key);
        mDatabase.child("FriendRequests").child(key).removeValue().addOnSuccessListener(deleteRequest -> {
            requestList.remove(position);
            requestAdapter.notifyItemRemoved(position);
        });
    }
}

