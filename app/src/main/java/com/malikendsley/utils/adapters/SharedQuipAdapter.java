package com.malikendsley.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.malikendsley.quipswap.R;
import com.malikendsley.utils.FirebaseHandler;
import com.malikendsley.utils.interfaces.PrivateQuipRetrievedListener;
import com.malikendsley.utils.schema.PrivateQuip;
import com.malikendsley.utils.schema.PublicQuip;

import java.util.ArrayList;

public class SharedQuipAdapter extends RecyclerView.Adapter<SharedQuipAdapter.SecureSharedQuipViewHolder> {

    ArrayList<PublicQuip> list;
    boolean isSent;
    Context context;
    FirebaseHandler mdb2;

    //if isSending = 1, populate with outgoing data, else incoming data
    public SharedQuipAdapter(boolean isSent, Context context, ArrayList<PublicQuip> list, Activity mActivity) {
        this.isSent = isSent;
        this.list = list;
        this.context = context;
        mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), mActivity);

    }

    @NonNull
    @Override
    public SecureSharedQuipViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_secure_sharedquip, parent, false);
        return new SecureSharedQuipViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SecureSharedQuipViewHolder holder, int position) {
        //list is populated externally
        PublicQuip sq = list.get(position);

        mdb2.UIDtoUsername(isSent ? sq.getRecipient() : sq.getSender(), holder.username::setText);
        String key = sq.getKey();
        mdb2.getQuipByKey(key, new PrivateQuipRetrievedListener() {
            @Override
            public void onRetrieveComplete(PrivateQuip quip) {
                StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(quip.getURI());
                final long FIVE_MEGABYTES = 1024 * 1024 * 5;

                httpsReference.getBytes(FIVE_MEGABYTES).addOnSuccessListener(bytes -> holder.Thumbnail.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length))).addOnFailureListener(e -> {
                    //Log.i(TAG, "URL Download Failed");
                    //e.printStackTrace();
                });

            }

            @Override
            public void onRetrieveFail(Exception e) {
                //Log.e(TAG, "onBind: Database Trouble");
                Toast.makeText(context, "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SecureSharedQuipViewHolder extends RecyclerView.ViewHolder {

        TextView username;
        ImageView Thumbnail;

        public SecureSharedQuipViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.usernameText);
            Thumbnail = itemView.findViewById(R.id.receivedThumbnail);

        }
    }
}
