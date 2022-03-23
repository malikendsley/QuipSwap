package com.malikendsley.quipswap.navfragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.malikendsley.quipswap.MakeQuipActivity;
import com.malikendsley.quipswap.R;

public class SentFragment extends Fragment {

    private static final String TAG = "Own";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FloatingActionButton fab = requireView().findViewById(R.id.fab);
        fab.setOnClickListener(view1 -> {
            Log.i(TAG, "Fab clicked");
            SentFragment.this.startActivity(new Intent(SentFragment.this.getContext(), MakeQuipActivity.class));
        });
    }
}
