package com.malikendsley.utils.adapters;

//import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malikendsley.quipswap.R;
import com.malikendsley.utils.interfaces.RequestClickListener;
import com.malikendsley.utils.schema.ExpandableListItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.SecureRequestViewHolder> {

    private static final String TAG = "Own";
    private final RequestClickListener listener;
    ArrayList<ExpandableListItem> list;

    public RequestAdapter(ArrayList<ExpandableListItem> list, RequestClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SecureRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_secure_friendrequest, parent, false);
        return new SecureRequestViewHolder(v, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SecureRequestViewHolder holder, int position) {
        //list is populated externally
        String requestUsername = (String) list.get(position).getObject();
        //Log.i(TAG, "onBind: " + requestUsername);

        holder.username.setText(requestUsername);
        holder.expandingSection.setVisibility(list.get(position).isExpanded() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SecureRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final WeakReference<RequestClickListener> listenerRef;
        TextView username;
        LinearLayout expandingSection;
        LinearLayout rowLayout;
        Button acceptButton;
        Button denyButton;

        public SecureRequestViewHolder(@NonNull View itemView, RequestClickListener listener) {
            super(itemView);
            //Log.i("Own", "Adding..");

            listenerRef = new WeakReference<>(listener);
            username = itemView.findViewById(R.id.requestUsername);

            expandingSection = itemView.findViewById(R.id.expandingRequestMenu);
            rowLayout = itemView.findViewById(R.id.rowLayout);
            acceptButton = itemView.findViewById(R.id.acceptFriendButton);
            denyButton = itemView.findViewById(R.id.denyFriendButton);

            acceptButton.setOnClickListener(this);
            denyButton.setOnClickListener(this);

            rowLayout.setOnClickListener(view -> {
                ExpandableListItem request = list.get(getAdapterPosition());
                request.setExpanded(!request.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == acceptButton.getId()) {
                //Log.i(TAG, "Accept Request in Adapter");
                listenerRef.get().onAcceptClicked(getAdapterPosition());
            } else if (v.getId() == denyButton.getId()) {
                //Log.i(TAG, "Deny Request in Adapter");
                listenerRef.get().onDenyClicked(getAdapterPosition());
            } else {
                //Log.i(TAG, "Request Adapter Problem");
            }
        }
    }
}