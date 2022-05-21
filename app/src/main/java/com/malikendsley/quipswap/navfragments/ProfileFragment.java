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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler2;
import com.malikendsley.firebaseutils.secureadapters.SecureFriendAdapter;
import com.malikendsley.firebaseutils.secureinterfaces.FriendAddListener;
import com.malikendsley.firebaseutils.secureinterfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.secureinterfaces.UserRetrievedListener;
import com.malikendsley.firebaseutils.secureschema.PrivateUser;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Own";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseHandler2 mdb2 = new FirebaseHandler2(mDatabase, getActivity());
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView friendRecycler;
    SecureFriendAdapter friendAdapter;

    TextView username;

    ArrayList<String> friendUIDList = new ArrayList<>();
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
        username = requireActivity().findViewById(R.id.profileUsernameText);

        //friend request setup
        Button addFriendButton = requireActivity().findViewById(R.id.addFriendButton);
        EditText friendSearch = requireActivity().findViewById(R.id.friendSearchUsername);

        //adding friends functionality
        addFriendButton.setOnClickListener(view1 -> {
            String friend = friendSearch.getText().toString();
            validateFriendUsername(friend);
        });

        friendSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                String friend = friendSearch.getText().toString();
                validateFriendUsername(friend);
                return true;
            } else {
                return false;
            }
        });

        //recycler setup
        friendRecycler = requireActivity().findViewById(R.id.friendList);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new SecureFriendAdapter(friendUIDList, position -> {
            //unused
        }, getActivity());
        friendRecycler.setAdapter(friendAdapter);

        //resolve user
        mdb2.getUser(mAuth.getUid(), new UserRetrievedListener() {
            @Override
            public void onUserRetrieved(PrivateUser user) {
                Log.i(TAG, "onUserRetrieved");
                Log.i(TAG, "username: " + user.getUsername());
                username.setText(user.getUsername());
            }

            @Override
            public void onRetrieveFailed(Exception e) {
                e.printStackTrace();
                //Log.i(TAG, "Profile: Resolve User failed");
                Toast.makeText(getContext(), "Profile: Having trouble connecting to database", Toast.LENGTH_SHORT).show();
            }
        });

        //populate friends
        mdb2.getFriends(new FriendRetrieveListener() {
            @Override
            public void onGetFriends(ArrayList<String> friendUIDList) {
                if (friendUIDList != null) {
                    ProfileFragment.this.friendUIDList.clear();
                    ProfileFragment.this.friendUIDList.addAll(friendUIDList);
                    dataFetched = true;
                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFailed(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Trouble retrieving friends", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAddFriend(String friend) {
        if (!dataFetched) {
            Toast.makeText(getContext(), "Please wait a moment and try again", Toast.LENGTH_SHORT).show();
            return;
        }
        mdb2.trySendFriendRequest(friendUIDList, friend, new FriendAddListener() {
            @Override
            public void onResult(String result) {
                Toast.makeText(getContext(), (result.equals("")) ? "Request Sent" : result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDatabaseException(Exception e) {
                //Log.i(TAG, "Database Error");
                e.printStackTrace();
                Toast.makeText(getContext(), "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateFriendUsername(String username) {
        if (username == null || username.equals("")) {
            //Log.i(TAG, "Empty Add Friend");
            Toast.makeText(getContext(), "Please specify a user", Toast.LENGTH_SHORT).show();
        } else {
            onAddFriend(username);
        }
    }
}
