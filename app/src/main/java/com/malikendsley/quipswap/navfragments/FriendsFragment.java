package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.malikendsley.firebaseutils.FriendAdapter;
import com.malikendsley.firebaseutils.FriendRequest;
import com.malikendsley.firebaseutils.Friendship;
import com.malikendsley.firebaseutils.RequestAdapter;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;
import java.util.Objects;


public class FriendsFragment extends Fragment {
    private static final String TAG = "Own";
    RecyclerView friendRecycler;
    FriendAdapter friendAdapter;

    RecyclerView requestRecycler;
    RequestAdapter requestAdapter;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    ArrayList<Friendship> friendList = new ArrayList<>();
    ArrayList<FriendRequest> requestList = new ArrayList<>();

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
        mAuth = FirebaseAuth.getInstance();

        //friend recycler setup
        friendRecycler = requireActivity().findViewById(R.id.friendList);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new FriendAdapter(friendList);
        friendRecycler.setAdapter(friendAdapter);

        //friend request recycler setup
        requestRecycler = requireActivity().findViewById(R.id.requestList);
        requestRecycler.setHasFixedSize(true);
        requestRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        requestAdapter = new RequestAdapter(requestList);
        requestRecycler.setAdapter(requestAdapter);

        Button addFriendButton = requireActivity().findViewById(R.id.addFriendButton);
        EditText friendSearch = requireActivity().findViewById(R.id.friendSearchUsername);

        addFriendButton.setOnClickListener(view1 -> {
            Log.i(TAG, "Add friend clicked");
            String username = friendSearch.getText().toString();
            //create new friend request
            tryAddFriend(username);
        });

        //retrieve friends and populate
        mDatabase.child("Friendships").orderByChild("User1").equalTo(mAuth.getUid()).get().addOnSuccessListener(friendSnapshot -> {
            for (DataSnapshot child : friendSnapshot.getChildren()) {
                /*TODO save things like this to disk to minimize reads*/
                friendList.add(child.getValue(Friendship.class));
            }
            //this is okay because the friends list once loaded will not change and can be bound all at once
            friendAdapter.notifyDataSetChanged();
        });

        //retrieve friend requests and populate
        mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(mAuth.getUid()).get().addOnSuccessListener(requestSnapshot -> {
            for (DataSnapshot child : requestSnapshot.getChildren()) {
                requestList.add(child.getValue(FriendRequest.class));
            }
            friendAdapter.notifyDataSetChanged();
        });
    }

    //TODO: replace username lookup with SharedPreferences Query (which will need populating)
    //TODO: order the conditions to query last, to minimize the chance of a database read
    void tryAddFriend(String username) {
        //check if username exists by leveraging taken usernames list
        mDatabase.child("TakenUsernames").child(username).get().addOnCompleteListener(doesExistTask -> {
            if (doesExistTask.isSuccessful()) {
                if (doesExistTask.getResult().getValue() != null) {
                    //user exists, check to see if friend is already in friends list
                    String friendUID = doesExistTask.getResult().getValue().toString();
                    //extra condition deny request if the username's associated UID matches our own
                    boolean validRequest = !friendUID.equals(mAuth.getUid());
                    //between this line and the next validRequest represents solely an own-request
                    if (!validRequest) {
                        Log.i(TAG, "Self-add detected");
                        Toast.makeText(getContext(), "You can't add yourself", Toast.LENGTH_SHORT).show();
                    }
                    for (Friendship friendship : friendList) {
                        if (friendship.getUser2().equals(friendUID)) {
                            //deny re-adding someone who is already a friend
                            validRequest = false;
                            Log.i(TAG, "FriendsFragment: Already friends with user " + friendship.getUser2());
                            Toast.makeText(getContext(), "Already friends with this user", Toast.LENGTH_SHORT).show();
                        }
                    }
                    //next condition to check is whether a friend request is already pending
                    if (validRequest) {
                        Log.i(TAG, "Valid Request, checking for existing requests");
                        mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(friendUID).get().addOnSuccessListener(matchingRequests -> {
                            if (matchingRequests.exists()) {
                                Log.i(TAG, "Requests to " + username + " found");
                                boolean isDuplicate = false;
                                for (DataSnapshot fr : matchingRequests.getChildren()) {
                                    FriendRequest mfr = fr.getValue(FriendRequest.class);
                                    //if among the friend requests ours is there its a dupe, no-go
                                    if (Objects.requireNonNull(mfr).getSender().equals(mAuth.getUid())) {
                                        Log.i(TAG, "Duplicate Request");
                                        isDuplicate = true;
                                    }
                                }
                                if (isDuplicate) {
                                    Toast.makeText(getContext(), "Already sent Request", Toast.LENGTH_SHORT).show();
                                } else {
                                    createRecord(friendUID);
                                }
                            } else {
                                Log.i(TAG, "No Requests to " + username + " found");
                                createRecord(friendUID);
                            }
                        });
                    }
                } else {
                    //case for user DNE
                    Log.i(TAG, "FriendsFragment: No such user");
                    Toast.makeText(getContext(), "This user does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                //case for general database failure
                Log.i(TAG, "FriendsFragment: Username check failed");
                Toast.makeText(getContext(), "FriendsFragment: Username check failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //simple helper function to clean things up
    void createRecord(String friendUID) {
        mDatabase.child("FriendRequests").push().setValue(new FriendRequest(mAuth.getUid(), friendUID));
        Log.i(TAG, "Request sent");
        Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
    }

    //TODO remove if ultimately unused (might implement functionality in the adapter instead)
    void acceptRequest(String RequestID) {
        //delete the request
        //add a new friendship
    }

    void rejectRequest(String RequestID) {
        //delete the request
    }
}
