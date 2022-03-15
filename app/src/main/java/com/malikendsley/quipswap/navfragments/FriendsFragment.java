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

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
            if(username.equals("")){
                Log.i(TAG, "Empty Add Friend");
                Toast.makeText(getContext(), "Please specify a user", Toast.LENGTH_SHORT).show();
            } else {
                tryAddFriend(username);
            }
        });

        Log.i(TAG, "Requesting friends");
        //retrieve friends and populate
        mDatabase.child("Friendships").orderByChild("User1").equalTo(mAuth.getUid()).get().addOnSuccessListener(user1snapshot -> {
            for (DataSnapshot child : user1snapshot.getChildren()) {
                /*TODO save things like this to disk to minimize reads*/
                friendList.add(child.getValue(Friendship.class));
            }
            //this is okay because the friends list once loaded will not change and can be bound all at once
            friendAdapter.notifyDataSetChanged();
        });
        mDatabase.child("Friendships").orderByChild("User2").equalTo(mAuth.getUid()).get().addOnSuccessListener(user2snapshot -> {
            for (DataSnapshot child : user2snapshot.getChildren()) {
                friendList.add(child.getValue(Friendship.class));
            }
            //this is okay because the friends list once loaded will not change and can be bound all at once
            friendAdapter.notifyDataSetChanged();
        });
        friendAdapter.notifyDataSetChanged();

        //retrieve friend requests and populate
        mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(mAuth.getUid()).get().addOnSuccessListener(requestSnapshot -> {
            for (DataSnapshot child : requestSnapshot.getChildren()) {
                FriendRequest fr = child.getValue(FriendRequest.class);
                Objects.requireNonNull(fr).setKey(child.getKey());
                requestList.add(fr);
            }
            friendAdapter.notifyDataSetChanged();
        });
    }

    //TODO: replace username lookup with SharedPreferences Query (which will need populating)

    void tryAddFriend(String username) {
        //user must exist
        //you cannot send 2 requests to the same person
        //you cannot request a person who has requested you already
        //you can't send a request to your friend or yourself

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
            //prevent adding if already friends
            for (Friendship friend : friendList) {
                if (friend.getUser2().equals(addUID) || friend.getUser1().equals(addUID)) {
                    Log.i(TAG, "Already friends");
                    Toast.makeText(getContext(), "Already friends with this user", Toast.LENGTH_SHORT).show();
                    return;
                }
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
                //all clear
                createRecord(addUID);
            });
        });

    }

    //simple helper function to clean things up
    void createRecord(String friendUID) {
        mDatabase.child("FriendRequests").push().setValue(new FriendRequest(mAuth.getUid(), friendUID));
        Log.i(TAG, "Request sent");
        Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
    }
}

