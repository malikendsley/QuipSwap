package com.malikendsley.firebaseutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.malikendsley.quipswap.R;

import java.io.IOException;
import java.util.ArrayList;

public class SharedQuipAdapter extends RecyclerView.Adapter<SharedQuipAdapter.SharedQuipViewHolder> {

    ArrayList<SharedQuip> list;
    Context context;

    public SharedQuipAdapter(Context context, ArrayList<SharedQuip> list) {
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
        Log.i("Own", sq.getURI());
        holder.Sender.setText(sq.getSender());
        Uri uri = Uri.parse(sq.getURI());

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.Thumbnail.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class SharedQuipViewHolder extends RecyclerView.ViewHolder {

        TextView Sender;
        ImageView Thumbnail;

        public SharedQuipViewHolder(@NonNull View itemView) {
            super(itemView);

            Sender = itemView.findViewById(R.id.receivedText);
            Thumbnail = itemView.findViewById(R.id.receivedThumbnail);

        }
    }
}
