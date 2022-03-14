package com.malikendsley.firebaseutils;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;
import java.util.Objects;


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
            if(requestSnapshot.exists()) {
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

    public class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView RID, username;
        LinearLayout expandingSection;
        LinearLayout rowLayout;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            RID = itemView.findViewById(R.id.requestID);
            username = itemView.findViewById(R.id.requestUsername);

            expandingSection = itemView.findViewById(R.id.expandingRequestMenu);
            rowLayout = itemView.findViewById(R.id.rowLayout);

            //TODO if scroll suddenly breaks this is why, go back to the deprecated function
            rowLayout.setOnClickListener(view -> {
                FriendRequest request = list.get(getBindingAdapterPosition());
                request.setExpandable(!request.isExpandable());
                notifyItemChanged(getBindingAdapterPosition());
            });

            itemView.findViewById(R.id.acceptFriendButton).setOnClickListener(view -> {
                Log.i(TAG, "Accept request from " + user.getUsername());

            });

            itemView.findViewById(R.id.denyFriendButton).setOnClickListener(view -> {
                String key = list.get(getBindingAdapterPosition()).getKey();
                Log.i(TAG, "Deny request from " + key);
                mDatabase.child("FriendRequests").child(key).removeValue().addOnSuccessListener(deleteRequest -> {
                    //notify user of deletion
                    Log.i(TAG, "Request Deleted");
                    Toast.makeText(itemView.getContext(), "Request Deleted", Toast.LENGTH_SHORT).show();
                    //has the desirable side effect of removing the request from the list
                    notifyItemChanged(getBindingAdapterPosition());
                    //TODO refresh the recycler right away, right now i have to navigate away and back
                });
            });
        }
    }

    void acceptRequest(){
    }

    void denyRequest(String key){

    }

    void deleteRequest(String key){

    }

}
