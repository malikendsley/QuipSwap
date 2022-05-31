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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.FriendAddListener;
import com.malikendsley.firebaseutils.interfaces.FriendRetrieveListener;
import com.malikendsley.firebaseutils.secureadapters.SecureFriendAdapter;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    //private static final String TAG = "Own";
    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), getActivity());
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    RecyclerView friendRecycler;
    SecureFriendAdapter friendAdapter;
    SwipeRefreshLayout swipeLayout;
    TextView username;

    ArrayList<String> friendUIDList = new ArrayList<>();
    boolean dataFetched = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        //profile data setup
        username = rootView.findViewById(R.id.profileUsernameText);

        //friend request setup
        Button addFriendButton = rootView.findViewById(R.id.addFriendButton);
        EditText friendSearch = rootView.findViewById(R.id.friendSearchUsername);

        //adding friends functionality
        addFriendButton.setOnClickListener(view1 -> {
            String friend = friendSearch.getText().toString();
            validateFriendUsername(friend);
        });

        //handle add friend submission
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
        friendRecycler = rootView.findViewById(R.id.friendList);
        friendRecycler.setHasFixedSize(true);
        friendRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        friendAdapter = new SecureFriendAdapter(friendUIDList, position -> {
            //unused
        }, getActivity());
        friendRecycler.setAdapter(friendAdapter);

        //resolve user
        mdb2.UIDtoUsername(mAuth.getUid(), user -> username.setText(user));

        //retrieve friends from server
        populateFriends();

        //swipe refresh functionality
        swipeLayout = rootView.findViewById(R.id.friendListSwipeRefresh);
        swipeLayout.setOnRefreshListener(this);

        return rootView;
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
                Toast.makeText(getContext(), "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateFriendUsername(String username) {
        if (username == null || username.equals("")) {
            Toast.makeText(getContext(), "Please specify a user", Toast.LENGTH_SHORT).show();
        } else {
            //Credit to Jennifer Wang my loyal tester
            onAddFriend(username.trim());
        }
    }

    @Override
    public void onRefresh() {
        populateFriends();
    }

    void populateFriends() {
        //populate friends
        mdb2.getFriends(new FriendRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onGetFriends(ArrayList<String> friendUIDList) {
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
                if (friendUIDList != null) {
                    ProfileFragment.this.friendUIDList.clear();
                    ProfileFragment.this.friendUIDList.addAll(friendUIDList);
                    dataFetched = true;
                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onGetFailed(Exception e) {
                Toast.makeText(getContext(), "Trouble retrieving friends", Toast.LENGTH_SHORT).show();
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        });
    }

}
