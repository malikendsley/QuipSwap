package com.malikendsley.firebaseutils.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.malikendsley.firebaseutils.schema.SharedQuip;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class SharedQuipAdapter extends RecyclerView.Adapter<SharedQuipAdapter.SharedQuipViewHolder> {

    static final String TAG = "Own";
    ArrayList<SharedQuip> list;
    boolean isSent;
    Context context;

    //if isSending = 1, populate with outgoing data, else incoming data
    public SharedQuipAdapter(boolean isSent, Context context, ArrayList<SharedQuip> list) {
        this.isSent = isSent;
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public SharedQuipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_sharedquip, parent, false);
        return new SharedQuipViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SharedQuipViewHolder holder, int position) {
        //list is populated externally
        SharedQuip sq = list.get(position);
        //Log.i("Own", "onBind: " + sq.toString());

        holder.UID.setText(isSent ? sq.Recipient : sq.Sender);

        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(sq.URI);
        final long FIVE_MEGABYTES = 1024 * 1024 * 5;

        httpsReference.getBytes(FIVE_MEGABYTES).addOnSuccessListener(bytes -> holder.Thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))).addOnFailureListener(e -> {
            Log.i(TAG, "URL Download Failed");
            e.printStackTrace();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SharedQuipViewHolder extends RecyclerView.ViewHolder {

        TextView UID;
        ImageView Thumbnail;

        public SharedQuipViewHolder(@NonNull View itemView) {
            super(itemView);

            UID = itemView.findViewById(R.id.UIDText);
            Thumbnail = itemView.findViewById(R.id.receivedThumbnail);

        }
    }
}
