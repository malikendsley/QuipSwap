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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Objects;


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
        EditText friendSearch = requireActivity().findViewById(R.id.friendSearchUsername);

        addFriendButton.setOnClickListener(view1 -> {
            Log.i(TAG, "Add friend clicked");
            //TODO Implement add friends
            String username = friendSearch.getText().toString();
            tryAddFriend(username);
            //create new friend request
        });

        //TODO find a way to display friend requests
        //likely either in line with friends (expand friendship class to include status?
        //or on separate menu (more de-normalization ugh)
        //load friendships into the list from database

        //TODO An option:
        //retrofit FriendAdapter to fill the recycler with "cards" which can contain either a friend
        //or a friend request, then fill them in order with friends at the top instead of
        //coercing a friend request into the existing friend adapter

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

    void tryAddFriend(String username){
        /*prevent adding people you're already friends with
          this may technically not work if a user manages to input a friend they already have added
          in the time it takes to load a friend, but this is rare and also shouldn't really cause
          any unrecoverable errors besides an odd friend request */
        Log.i(TAG, "Current contents of list: \n" + list.toString());
        mDatabase.child("TakenUsernames").child(username).get().addOnCompleteListener(doesExistTask -> {
            if(doesExistTask.isSuccessful()){
                if (doesExistTask.getResult().getValue() != null) {
                    //user exists, check to see if friend is already in friends list
                    String UID = doesExistTask.getResult().getValue().toString();
                    Log.i(TAG, "User located, " + UID);
                    mDatabase.child(Objects.requireNonNull(mAuth.getUid())).child("Friends").child(UID).get().addOnCompleteListener(noDupTask -> {
                        if(noDupTask.isSuccessful()){
                            if(noDupTask.getResult().getValue() == null){
                                Log.i(TAG,"User not already added");
                                //check if a friend request is already pending
                                //query for Friend Request where UID matches given
                            } else {
                                Log.i(TAG, "FriendsFragment: User already friends");
                                Toast.makeText(getContext(), "You are already friends with " + username, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.i(TAG, "FriendsFragment: Username check failed");
                            Toast.makeText(getContext(), "FriendsFragment: Username check failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.i(TAG, "FriendsFragment: No such user");
                    Toast.makeText(getContext(), "This user does not exist", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.i(TAG, "FriendsFragment: Username check failed");
                Toast.makeText(getContext(), "FriendsFragment: Username check failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}