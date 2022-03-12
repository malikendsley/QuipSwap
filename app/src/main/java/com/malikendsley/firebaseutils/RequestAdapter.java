package com.malikendsley.firebaseutils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private static final String TAG = "Own";
    ArrayList<FriendRequest> list;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    User user = new User();

    public RequestAdapter(ArrayList<FriendRequest> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friendrequest, parent, false);
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

        boolean isExpandable = list.get(position).isExpandable();
        holder.expandingSection.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView RID, username;
        Button DenyButton, AcceptButton;
        LinearLayout expandingSection;
        LinearLayout rowLayout;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            RID = itemView.findViewById(R.id.requestID);
            username = itemView.findViewById(R.id.requestUsername);
            AcceptButton = itemView.findViewById(R.id.acceptFriendButton);
            DenyButton = itemView.findViewById(R.id.denyFriendButton);

            expandingSection = itemView.findViewById(R.id.expandingRequestMenu);
            rowLayout = itemView.findViewById(R.id.rowLayout);

            rowLayout.setOnClickListener(view -> {
                FriendRequest request = list.get(getAdapterPosition());
                request.setExpandable(!request.isExpandable());
                notifyItemChanged(getAdapterPosition());
            });

        }
    }
}
