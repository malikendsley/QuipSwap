package com.malikendsley.firebaseutils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private static final String TAG = "Own";
    Context context;
    ArrayList<FriendRequest> list;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    User user = new User();

    public RequestAdapter(Context context, ArrayList<FriendRequest> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item_friendrequest, parent, false);
        return new RequestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        //list is populated externally
        FriendRequest request = list.get(position);
        //search the database for this user
        mDatabase.child("Users").child(request.getSender()).get().addOnSuccessListener(requestSnapshot -> {
            //populate the card with this user's data
            user = requestSnapshot.getValue(User.class);
            holder.RID.setText(request.getSender());
            holder.username.setText(user.getUsername());
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView RID, username;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            RID = itemView.findViewById(R.id.requestID);
            username = itemView.findViewById(R.id.requestUsername);
        }
    }
}
