package com.malikendsley.firebaseutils.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.schema.Friendship;
import com.malikendsley.firebaseutils.schema.User;
import com.malikendsley.firebaseutils.interfaces.RowClickListener;
import com.malikendsley.quipswap.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {
    private static final String TAG = "Own";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final RowClickListener listener;
    ArrayList<Friendship> list;
    User user = new User();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public FriendAdapter(ArrayList<Friendship> list, RowClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(v, listener);
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
            holder.FID.setText(correctID);
            holder.username.setText(user.getUsername());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final WeakReference<RowClickListener> listenerRef;
        TextView FID, username;
        CardView row;

        public FriendViewHolder(@NonNull View itemView, RowClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);

            FID = itemView.findViewById(R.id.friendID);
            username = itemView.findViewById(R.id.friendUsername);
            row = itemView.findViewById(R.id.friendRow);

            row.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            row.setClickable(false);
            if (v.getId() == row.getId()) {
                Log.i(TAG, "Row " + getAdapterPosition() + " clicked");
                listenerRef.get().onRowClicked(getAdapterPosition());
            } else {
                Log.i(TAG, "Friend Adapter Problem");
            }
        }
    }
}
