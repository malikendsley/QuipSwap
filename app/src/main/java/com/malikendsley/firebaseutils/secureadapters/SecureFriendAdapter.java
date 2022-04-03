package com.malikendsley.firebaseutils.secureadapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler2;
import com.malikendsley.firebaseutils.interfaces.RowClickListener;
import com.malikendsley.quipswap.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class SecureFriendAdapter extends RecyclerView.Adapter<SecureFriendAdapter.SecureFriendViewHolder> {
    private static final String TAG = "Own";
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private final RowClickListener listener;
    private final Activity mActivity;
    ArrayList<String> list;
    
    public SecureFriendAdapter(ArrayList<String> list, RowClickListener listener, Activity mActivity) {
        this.list = list;
        this.listener = listener;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public SecureFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friend, parent, false);
        return new SecureFriendViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SecureFriendViewHolder holder, int position) {
        FirebaseHandler2 mdb2 = new FirebaseHandler2(mDatabase, mActivity);

        //list is populated externally
        String friend = list.get(position);
        //get username from Username aync
        mdb2.UIDtoUsername(friend, holder.username::setText);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SecureFriendViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final WeakReference<RowClickListener> listenerRef;
        TextView FID, username;
        CardView row;

        public SecureFriendViewHolder(@NonNull View itemView, RowClickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<>(listener);

            username = itemView.findViewById(R.id.secureFriendUsername);
            row = itemView.findViewById(R.id.secureFriendRow);

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
