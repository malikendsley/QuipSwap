package com.malikendsley.quipswap;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    private EditText emailField;
    private EditText passwordField;
    private Button signInButton;
    private Button signUpButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        emailField = (EditText) getView().findViewById(R.id.signupEmail);
        passwordField = (EditText) getView().findViewById(R.id.signUpPassword);
        signInButton = (Button) getView().findViewById(R.id.signInButton);
        signUpButton = (Button) getView().findViewById(R.id.signUpButton);

    }
}
