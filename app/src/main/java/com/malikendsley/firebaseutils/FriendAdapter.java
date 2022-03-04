package com.malikendsley.firebaseutils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;
import java.util.Objects;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder>{
    private static final String TAG = "Own";

    Context context;
    ArrayList<Friendship> list;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    User user = new User();


    public FriendAdapter(Context context, ArrayList<Friendship> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.friend, parent, false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Log.i("Own", "Binding friend");
        //get the particular friendship
        Friendship friendship = list.get(position);
        //search the users table for this person
        mDatabase.child("Users").child(friendship.getUser2()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                //fill their details into the textview
                Log.i(TAG, "Data Retrieved: " + Objects.requireNonNull(task.getResult().getValue()));
                user = task.getResult().getValue(User.class);
                holder.RID.setText(friendship.getUser2());
                holder.username.setText(user.getUsername());

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder{

        TextView RID, username;

        public FriendViewHolder(@NonNull View itemView){
            super(itemView);

            RID = itemView.findViewById(R.id.friendID);
            username = itemView.findViewById(R.id.friendUsername);
        }
    }
}
