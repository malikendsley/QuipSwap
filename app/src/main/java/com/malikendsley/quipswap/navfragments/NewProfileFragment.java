package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.adapters.FriendAdapter;
import com.malikendsley.firebaseutils.interfaces.FriendAddListener;
import com.malikendsley.firebaseutils.interfaces.UserRetrievedListener;
import com.malikendsley.firebaseutils.schema.Friendship;
import com.malikendsley.firebaseutils.schema.User;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

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
        email = requireActivity().findViewById(R.id.profileEmailText);

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

    private void onAddFriend(String friend) {
        mdb.tryAddFriend(friend, new FriendAddListener() {
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
        if (!dataFetched) {
            Toast.makeText(getContext(), "Please wait a moment before trying again", Toast.LENGTH_SHORT).show();
            return;
        }
        if (username == null || username.equals("")) {
            //Log.i(TAG, "Empty Add Friend");
            Toast.makeText(getContext(), "Please specify a user", Toast.LENGTH_SHORT).show();
        } else {
            onAddFriend(username);
        }
    }
}
