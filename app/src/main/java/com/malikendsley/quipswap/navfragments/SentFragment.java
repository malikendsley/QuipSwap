package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.adapters.SharedQuipAdapter;
import com.malikendsley.firebaseutils.interfaces.QuipRetrieveListener;
import com.malikendsley.firebaseutils.schema.SharedQuip;
import com.malikendsley.quipswap.MakeQuipActivity;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class SentFragment extends Fragment {

    private static final String TAG = "Own";
    FirebaseHandler mdb;
    DatabaseReference mDatabase;

    TextView noSentText;
    RecyclerView sentRecycler;
    SharedQuipAdapter sharedQuipAdapter;

    ArrayList<SharedQuip> sharedQuipList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //TODO migrate

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mdb = new FirebaseHandler(mDatabase);

        //flavor
        noSentText = requireActivity().findViewById(R.id.noSentSwapsText);

        //sent recycler setup
        sentRecycler = requireActivity().findViewById(R.id.sentSwapsRecycler);
        sentRecycler.setHasFixedSize(true);
        sentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedQuipAdapter = new SharedQuipAdapter(true, getContext(), sharedQuipList);
        sentRecycler.setAdapter(sharedQuipAdapter);

        //fab
        requireView().findViewById(R.id.fab).setOnClickListener(view1 -> {
            Log.i(TAG, "Fab clicked");
            SentFragment.this.startActivity(new Intent(SentFragment.this.getContext(), MakeQuipActivity.class));
        });

        mdb.retrieveSentQuips(new QuipRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRetrieveComplete(ArrayList<SharedQuip> quips) {
                Log.i(TAG, "Retrieve Complete");
                //populate
                sharedQuipList.clear();
                sharedQuipList.addAll(quips);
                //notify
                if(!sharedQuipList.isEmpty()){
                    sharedQuipAdapter.notifyDataSetChanged();
                    noSentText.setVisibility(View.GONE);
                } else {
                    noSentText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRetrieveFail(Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Retrieve failed");
            }
        });
    }
}
