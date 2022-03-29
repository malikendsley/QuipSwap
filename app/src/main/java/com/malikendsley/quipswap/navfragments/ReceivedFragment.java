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

public class ReceivedFragment extends Fragment {

    private static final String TAG = "Own";
    FirebaseHandler mdb;
    DatabaseReference mDatabase;

    TextView noReceivedText;
    RecyclerView receivedRecycler;
    SharedQuipAdapter sharedQuipAdapter;

    ArrayList<SharedQuip> sharedQuipList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_received, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mdb = new FirebaseHandler(mDatabase);

        //flavor
        noReceivedText = requireActivity().findViewById(R.id.noReceivedSwapsText);

        //received recycler setup
        receivedRecycler = requireActivity().findViewById(R.id.receivedSwapsRecycler);
        receivedRecycler.setHasFixedSize(true);
        receivedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedQuipAdapter = new SharedQuipAdapter(false, getContext(), sharedQuipList);
        receivedRecycler.setAdapter(sharedQuipAdapter);

        //fab
        requireView().findViewById(R.id.fab).setOnClickListener(view1 -> {
            Log.i(TAG, "Fab clicked");
            ReceivedFragment.this.startActivity(new Intent(ReceivedFragment.this.getContext(), MakeQuipActivity.class));
        });

        mdb.retrieveReceivedQuips(new QuipRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRetrieveComplete(ArrayList<SharedQuip> quips) {
                Log.i(TAG, "Retrieve Complete");
                //populate quip adapter
                sharedQuipList.clear();
                sharedQuipList.addAll(quips);
                //notify
                if(!sharedQuipList.isEmpty()){
                    sharedQuipAdapter.notifyDataSetChanged();
                    noReceivedText.setVisibility(View.GONE);
                } else {
                    noReceivedText.setVisibility(View.VISIBLE);
                }
                //TODO: speed this up, discovering how to "realtime" this would improve every adapter in the app
            }

            @Override
            public void onRetrieveFail(Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Retrieve failed");
            }
        });
    }
}
