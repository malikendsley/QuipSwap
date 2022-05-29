package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.PublicQuipRetrieveListener;
import com.malikendsley.firebaseutils.secureadapters.SecureSharedQuipAdapter;
import com.malikendsley.firebaseutils.secureschema.PublicQuip;
import com.malikendsley.quipswap.MakeQuipActivity;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class ReceivedFragment extends Fragment {

    private static final String TAG = "Own";
    FirebaseHandler mdb2;

    TextView noReceivedText;
    RecyclerView receivedRecycler;
    SecureSharedQuipAdapter sharedQuipAdapter;

    ArrayList<PublicQuip> sharedQuipList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_received, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), getActivity());

        //flavor
        noReceivedText = requireActivity().findViewById(R.id.noReceivedSwapsText);

        //received recycler setup
        receivedRecycler = requireActivity().findViewById(R.id.receivedSwapsRecycler);
        receivedRecycler.setHasFixedSize(true);
        receivedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedQuipAdapter = new SecureSharedQuipAdapter(false, getContext(), sharedQuipList, getActivity());
        receivedRecycler.setAdapter(sharedQuipAdapter);

        //fab
        requireView().findViewById(R.id.fab).setOnClickListener(view1 -> {
            //Log.i(TAG, "Fab clicked");
            ReceivedFragment.this.startActivity(new Intent(ReceivedFragment.this.getContext(), MakeQuipActivity.class));
        });


        mdb2.getReceivedQuips(new PublicQuipRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRetrieveComplete(ArrayList<PublicQuip> quipList) {
                if (!quipList.isEmpty()) {
                    sharedQuipList.clear();
                    sharedQuipList.addAll(quipList);
                    sharedQuipAdapter.notifyDataSetChanged();
                    noReceivedText.setVisibility(View.GONE);
                } else {
                    noReceivedText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onRetrieveFail(Exception e) {
                //e.printStackTrace();
                //Toast.makeText(getContext(), "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}