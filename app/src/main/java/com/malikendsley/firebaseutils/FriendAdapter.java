package com.malikendsley.firebaseutils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private static final String TAG = "Own";

    ArrayList<Friendship> list;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    User user = new User();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public FriendAdapter(ArrayList<Friendship> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        //list is populated externally
        Friendship friendship = list.get(position);
        //figure out which user is not us
        String correctID = friendship.getUser1().equals(mAuth.getUid()) ? friendship.getUser2() : friendship.getUser1();
        mDatabase.child("Users").child(correctID).get().addOnSuccessListener(userSnapshot -> {
            //fill their details into the textview
            //Log.i(TAG, "Data Retrieved: " + Objects.requireNonNull(task.getResult().getValue()));
            user = userSnapshot.getValue(User.class);
            //get whichever user is not you
            holder.FID.setText(friendship.getUser2());
            holder.username.setText(user.getUsername());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {

        TextView FID, username;

        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);

            FID = itemView.findViewById(R.id.friendID);
            username = itemView.findViewById(R.id.friendUsername);
        }
    }
}
