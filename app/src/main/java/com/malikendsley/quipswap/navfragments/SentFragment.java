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

public class SentFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), getActivity());

    TextView noSentText;
    RecyclerView sentRecycler;
    SecureSharedQuipAdapter sharedQuipAdapter;
    SwipeRefreshLayout swipeLayout;

    ArrayList<PublicQuip> sharedQuipList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sent, container, false);

        //flavor
        noSentText = rootView.findViewById(R.id.noSentSwapsText);

        //sent recycler setup
        sentRecycler = rootView.findViewById(R.id.sentSwapsRecycler);
        sentRecycler.setHasFixedSize(true);
        sentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedQuipAdapter = new SecureSharedQuipAdapter(true, getContext(), sharedQuipList, getActivity());
        sentRecycler.setAdapter(sharedQuipAdapter);

        //fab
        rootView.findViewById(R.id.fab).setOnClickListener(view1 -> SentFragment.this.startActivity(new Intent(SentFragment.this.getContext(), MakeQuipActivity.class)));

        //set up swipe to refresh
        swipeLayout = rootView.findViewById(R.id.sentSwipeRefresh);
        swipeLayout.setOnRefreshListener(this);

        //populate sent quips
        updateSent();

        return rootView;
    }

    void updateSent() {
        mdb2.getSentQuips(new PublicQuipRetrieveListener() {
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
                    noSentText.setVisibility(View.GONE);
                } else {
                    noSentText.setVisibility(View.VISIBLE);
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
        updateSent();
    }
}
