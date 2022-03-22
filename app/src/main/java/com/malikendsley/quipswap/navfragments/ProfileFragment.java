package com.malikendsley.quipswap.navfragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.malikendsley.firebaseutils.User;
import com.malikendsley.quipswap.MainActivity;
import com.malikendsley.quipswap.R;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final String TAG = "Own";

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        Button logoutButton = requireView().findViewById(R.id.logOutButton);
        TextView username = requireView().findViewById(R.id.usernameText);
        TextView email = requireView().findViewById(R.id.emailText);

        ValueEventListener profileListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Log.i(TAG, "Data retrieved: " + user);
                username.setText(user.getUsername());
                email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
                Toast.makeText(getContext(), "ProfileFragment: Failed to fetch profile data", Toast.LENGTH_SHORT).show();

            }
        };

        mDatabase.child("Users").child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(profileListener);

        logoutButton.setOnClickListener(view1 -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getActivity(), "Logged out", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            requireActivity().finish();
        });

    }
}