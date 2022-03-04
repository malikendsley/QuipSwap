package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikendsley.firebaseutils.FriendAdapter;
import com.malikendsley.firebaseutils.Friendship;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    private static final String TAG = "Own";
    RecyclerView recyclerView;
    DatabaseReference mDatabase;
    FriendAdapter friendAdapter;
    ArrayList<Friendship> list;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        //recycler setup
        recyclerView = requireActivity().findViewById(R.id.friendList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        friendAdapter = new FriendAdapter(getContext(), list);
        recyclerView.setAdapter(friendAdapter);
        Log.i(TAG, "Friends Loaded");

        Button addFriendButton = requireActivity().findViewById(R.id.addFriendButton);

        addFriendButton.setOnClickListener(view1 -> {
            Log.i(TAG, "Add friend clicked");
            //TODO Implement add friends
            //check if user exists
            //create new friend request
            //maybe update the recycler
        });

        //TODO find a way to display friend requests
        //likely either in line with friends (expand friendship class to include status?
        //or on separate menu (more denormalization ugh)

        //load friendships into the list from database
        mDatabase.child("Friendships").orderByChild("User1").equalTo(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Friendship friend = dataSnapshot.getValue(Friendship.class);
                    //TODO this list should probably be saved to disk once populated
                    //in order to cut down on reads
                    list.add(friend);
                    Log.i(TAG, "Loading friend");
                }
                //this is okay because the friends list once loaded will not change and can be bound all at once
                friendAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}