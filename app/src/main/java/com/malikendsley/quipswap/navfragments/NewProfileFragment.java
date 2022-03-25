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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.adapters.FriendAdapter;
import com.malikendsley.firebaseutils.interfaces.UserRetrievedListener;
import com.malikendsley.firebaseutils.schema.FriendRequest;
import com.malikendsley.firebaseutils.schema.Friendship;
import com.malikendsley.firebaseutils.schema.User;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;
import java.util.Objects;

import javax.annotation.Nullable;

public class NewProfileFragment extends Fragment {

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseHandler mdb = new FirebaseHandler(mDatabase);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    RecyclerView friendRecycler;
    FriendAdapter friendAdapter;

    TextView username;
    TextView email;

    ArrayList<Friendship> friendList = new ArrayList<>();
    boolean dataFetched = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //profile data setup
        username = requireActivity().findViewById(R.id.usernameText);
        email = requireActivity().findViewById(R.id.passwordText);

        //friend request setup
        Button addFriendButton = requireActivity().findViewById(R.id.addFriendButton);
        EditText friendSearch = requireActivity().findViewById(R.id.friendSearchUsername);

        //adding friends functionality
        addFriendButton.setOnClickListener(view1 -> {
            //Log.i(TAG, "Add friend clicked");
            //create new friend request
            //validateFriendUsername(friendSearch.getText().toString());
        });
        friendSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                //TODO add this
                //Log.i(TAG, "Add friend clicked via soft keyboard");
                //tryAddFriend(friendSearch.getText().toString());
                return true;
            } else {
                return false;
            }
        });

        //recycler setup
        friendRecycler = requireActivity().findViewById(R.id.friendList);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new FriendAdapter(friendList, position -> {
            //unused
        });
        friendRecycler.setAdapter(friendAdapter);


        //resolve user
        mdb.retrieveUser(mAuth.getUid(), new UserRetrievedListener() {
            @Override
            public void onUserRetrieved(User user) {
                username.setText(user.Username);
                email.setText(user.Email);
            }

            @Override
            public void onRetrieveFailed(Exception e) {
                e.printStackTrace();
                //Log.i(TAG, "Profile: Resolve User failed");
                Toast.makeText(getContext(), "Having trouble connecting to database", Toast.LENGTH_SHORT).show();
            }
        });

        //populate friends
        mdb.retrieveFriends(friendsList -> {
            //Log.i(TAG, "Adapter: Friends Retrieved");
            if (friendsList != null) {
                friendList.clear();
                friendList.addAll(friendsList);
                dataFetched = true;
                //Log.i(TAG, friendList.toString());
            } else {
                //Log.i(TAG, "Retrieved Null");
            }
            friendAdapter.notifyDataSetChanged();
        });
    }

    private void validateFriendUsername(String username) {
        if (username == null || username.equals("")) {
            //Log.i(TAG, "Empty Add Friend");
            Toast.makeText(getContext(), "Please specify a user", Toast.LENGTH_SHORT).show();
        } else {
            tryAddFriend(username);
        }
    }

    void tryAddFriend(String username) {
        //user must exist
        //you cannot send 2 requests to the same person
        //you cannot request a person who has requested you already
        //you can't send a request to your friend or yourself

        //unlikely edge case handling
        //Log.i(TAG, "Check load friends");
        if (!dataFetched) {
            //Log.i(TAG, "Friends not loaded");
            Toast.makeText(getContext(), "Please wait a moment then try again", Toast.LENGTH_SHORT).show();
            return;
        }
        mdb.resolveUsername(username, resolvedUID -> {
            if (resolvedUID == null) {
                //Log.i(TAG, "FriendsFragment: No such user");
                Toast.makeText(getContext(), "This user does not exist", Toast.LENGTH_SHORT).show();
                return;
            }
            //Log.i(TAG, "addUID = " + resolvedUID);
            //prevent self add
            if (resolvedUID.equals(mAuth.getUid())) {
                //Log.i(TAG, "Self-add detected");
                Toast.makeText(getContext(), "You can't add yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            //TODO prevent adding if incoming request exists
//            for (FriendRequest request : requestList) {
//                if (request.getSender().equals(resolvedUID)) {
//                    //Log.i(TAG, "Cross-send attempt");
//                    Toast.makeText(getContext(), "Accept the pending request instead", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            }
            //prevent adding if outgoing request exists
            mDatabase.child("FriendRequests").orderByChild("Recipient").equalTo(resolvedUID).get().addOnSuccessListener(matchingIncoming -> {
                if (matchingIncoming.exists()) {
                    for (DataSnapshot fr : matchingIncoming.getChildren()) {
                        FriendRequest mfr = fr.getValue(FriendRequest.class);
                        if (Objects.requireNonNull(mfr).getSender().equals(mAuth.getUid())) {
                            //Log.i(TAG, "Already outgoing");
                            Toast.makeText(getContext(), "Already sent Request", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
                //don't add people you're already friends with
                mdb.retrieveFriends(friendsList -> {
                    for (Friendship friend : friendsList) {
                        if (friend.getUser1().equals(resolvedUID) || friend.getUser2().equals(resolvedUID)) {
                            //Log.i(TAG, "Already friends");
                            Toast.makeText(getContext(), "Already friends with this user", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    //all clear
                    //Log.i(TAG, "Request created");
                    createRecord(resolvedUID);
                });
            });
        });
    }

    //simple helper function to clean things up
    void createRecord(String friendUID) {
        mDatabase.child("FriendRequests").push().setValue(new FriendRequest(mAuth.getUid(), friendUID));
        //Log.i(TAG, "Request sent");
        Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_SHORT).show();
    }


    //add friend setup TODO firebaseHandler
}
