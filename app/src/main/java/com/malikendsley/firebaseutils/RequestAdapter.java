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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private static final String TAG = "Own";
    ArrayList<FriendRequest> list;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    User user = new User();
    private final RequestClickListener listener;

    public RequestAdapter(ArrayList<FriendRequest> list, RequestClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friendrequest, parent, false);
        return new RequestViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        //list is populated externally
        FriendRequest request = list.get(position);
        //search the database for this user
        mDatabase.child("Users").child(request.getSender()).get().addOnSuccessListener(requestSnapshot -> {
            //populate the card with this user's data
            if (requestSnapshot.exists()) {
                user = requestSnapshot.getValue(User.class);
                Objects.requireNonNull(user).setUID(request.getRecipient());
                holder.RID.setText(request.getSender());
                holder.username.setText(user.getUsername());
            } else {
                Log.i(TAG, "DB Read failed");
            }
        });

        boolean isExpandable = list.get(position).isExpandable();
        holder.expandingSection.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView RID, username;
        LinearLayout expandingSection;
        LinearLayout rowLayout;
        Button acceptButton;
        Button denyButton;

        private final WeakReference<RequestClickListener> listenerRef;

        public RequestViewHolder(@NonNull View itemView, RequestClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);
            RID = itemView.findViewById(R.id.requestID);
            username = itemView.findViewById(R.id.requestUsername);

            expandingSection = itemView.findViewById(R.id.expandingRequestMenu);
            rowLayout = itemView.findViewById(R.id.rowLayout);
            acceptButton = itemView.findViewById(R.id.acceptFriendButton);
            denyButton = itemView.findViewById(R.id.denyFriendButton);

            acceptButton.setOnClickListener(this);
            denyButton.setOnClickListener(this);

            rowLayout.setOnClickListener(view -> {
                FriendRequest request = list.get(getBindingAdapterPosition());
                request.setExpandable(!request.isExpandable());
                notifyItemChanged(getBindingAdapterPosition());
            });

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == acceptButton.getId()) {
                Log.i(TAG, "Accept Request in Adapter");
                listenerRef.get().onAcceptClicked(getBindingAdapterPosition());
            } else if (v.getId() == denyButton.getId()) {
                Log.i(TAG, "Deny Request in Adapter");
                listenerRef.get().onDenyClicked(getBindingAdapterPosition());
            } else {
                Log.i(TAG, "Request Adapter Problem");
            }
        }
    }
}
