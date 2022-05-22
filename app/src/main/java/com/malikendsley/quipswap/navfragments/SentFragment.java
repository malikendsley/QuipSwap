package com.malikendsley.quipswap.navfragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler2;
import com.malikendsley.firebaseutils.secureadapters.SecureSharedQuipAdapter;
import com.malikendsley.firebaseutils.secureinterfaces.PublicQuipRetrieveListener;
import com.malikendsley.firebaseutils.secureschema.PublicQuip;
import com.malikendsley.quipswap.MakeQuipActivity;
import com.malikendsley.quipswap.R;

import java.util.ArrayList;

public class SentFragment extends Fragment {

    private static final String TAG = "Own";
    FirebaseHandler2 mdb2;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    TextView noSentText;
    RecyclerView sentRecycler;
    SecureSharedQuipAdapter sharedQuipAdapter;

    ArrayList<PublicQuip> sharedQuipList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mdb2 = new FirebaseHandler2(mDatabase, getActivity());

        //flavor
        noSentText = requireActivity().findViewById(R.id.noSentSwapsText);

        //sent recycler setup
        sentRecycler = requireActivity().findViewById(R.id.sentSwapsRecycler);
        sentRecycler.setHasFixedSize(true);
        sentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        sharedQuipAdapter = new SecureSharedQuipAdapter(true, getContext(), sharedQuipList, getActivity());
        sentRecycler.setAdapter(sharedQuipAdapter);

        //fab
        requireView().findViewById(R.id.fab).setOnClickListener(view1 -> {
            Log.i(TAG, "Fab clicked");
            SentFragment.this.startActivity(new Intent(SentFragment.this.getContext(), MakeQuipActivity.class));
        });

        mdb2.getSentQuips(new PublicQuipRetrieveListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRetrieveComplete(ArrayList<PublicQuip> quipList) {
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
                e.printStackTrace();
                //Toast.makeText(getContext(), "Trouble connecting to the database", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
