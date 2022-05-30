package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.PublicQuipRetrieveListener;
import com.malikendsley.firebaseutils.secureadapters.SecureSharedQuipAdapter;
import com.malikendsley.firebaseutils.secureschema.PublicQuip;
import com.malikendsley.quipswap.MakeQuipActivity;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class ReceivedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), getActivity());

    TextView noReceivedText;
    RecyclerView receivedRecycler;
    SecureSharedQuipAdapter sharedQuipAdapter;
    SwipeRefreshLayout swipeLayout;

    ArrayList<PublicQuip> sharedQuipList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_received, container, false);

        //flavor
        noReceivedText = rootView.findViewById(R.id.noReceivedSwapsText);

        //received recycler setup
        receivedRecycler = rootView.findViewById(R.id.receivedSwapsRecycler);
        receivedRecycler.setHasFixedSize(true);
        receivedRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedQuipAdapter = new SecureSharedQuipAdapter(false, getContext(), sharedQuipList, getActivity());
        receivedRecycler.setAdapter(sharedQuipAdapter);

        //fab
        rootView.findViewById(R.id.fab).setOnClickListener(view1 -> {
            //Log.i(TAG, "Fab clicked");
            ReceivedFragment.this.startActivity(new Intent(ReceivedFragment.this.getContext(), MakeQuipActivity.class));
        });

        //set up swipe to refresh
        swipeLayout = rootView.findViewById(R.id.receivedSwipeRefresh);
        swipeLayout.setOnRefreshListener(this);

        //populate the list of received Quips
        updateReceived();

        return rootView;
    }

    void updateReceived() {
        mdb2.getReceivedQuips(new PublicQuipRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRetrieveComplete(ArrayList<PublicQuip> quipList) {
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
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
                if (swipeLayout.isRefreshing()) {
                    swipeLayout.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        updateReceived();
    }
}