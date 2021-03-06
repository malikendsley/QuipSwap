package com.malikendsley.quipswap.navfragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.malikendsley.quipswap.MainActivity;
import com.malikendsley.quipswap.R;
import com.malikendsley.quipswap.SignupActivity;

import java.util.Objects;

public class SignInFragment extends Fragment {


    private EditText emailField;
    private EditText passwordField;

    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        emailField = requireView().findViewById(R.id.signupEmail);
        passwordField = requireView().findViewById(R.id.signUpPassword);
        Button signInButton = requireView().findViewById(R.id.signInButton);
        Button signUpButton = requireView().findViewById(R.id.signUpButton);

        auth = FirebaseAuth.getInstance();

        passwordField.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                validateUser(emailField.getText().toString(), passwordField.getText().toString());
                return true;
            } else {
                return false;
            }
        });

        signUpButton.setOnClickListener(signUpView -> {
            Intent intent = new Intent(getContext(), SignupActivity.class);
            startActivity(intent);
        });

        signInButton.setOnClickListener(view1 -> {
            //validate then try to log in
            validateUser(emailField.getText().toString(), passwordField.getText().toString());
        });
    }

    private void validateUser(String email, String password) {
        if (email.matches("") || password.matches("")) {
            Toast.makeText(getContext(), "You must provide both an email and a password", Toast.LENGTH_SHORT).show();
        } else {
            loginUser(email, password);
        }
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email.trim(), password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Login Successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                requireActivity().finish();
            } else {
                String code = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                switch (code) {

                    case "ERROR_INVALID_CUSTOM_TOKEN":
                        Toast.makeText(getContext(), "The custom token format is incorrect. Please check the documentation.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_CUSTOM_TOKEN_MISMATCH":
                        Toast.makeText(getContext(), "The custom token corresponds to a different audience.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_CREDENTIAL":
                        Toast.makeText(getContext(), "The supplied auth credential is malformed or has expired.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_INVALID_EMAIL":
                        //Toast.makeText(getContext(), "The email is invalid", Toast.LENGTH_SHORT).show();
                        emailField.setError("please enter a valid email");
                        emailField.requestFocus();
                        break;

                    case "ERROR_WRONG_PASSWORD":
                        //Toast.makeText(getContext(), "The password is invalid", Toast.LENGTH_SHORT).show();
                        passwordField.setError("password is incorrect");
                        passwordField.requestFocus();
                        passwordField.setText("");
                        break;

                    case "ERROR_USER_MISMATCH":
                        Toast.makeText(getContext(), "The supplied credentials do not correspond to the previously signed in user.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_REQUIRES_RECENT_LOGIN":
                        Toast.makeText(getContext(), "This operation is sensitive and requires recent authentication. Log in again before retrying this request.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                        Toast.makeText(getContext(), "An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_DISABLED":
                        Toast.makeText(getContext(), "The user account has been disabled by an administrator.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_TOKEN_EXPIRED":

                    case "ERROR_INVALID_USER_TOKEN":
                        Toast.makeText(getContext(), "The user\\'s credential is no longer valid. The user must sign in again.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_USER_NOT_FOUND":
                        emailField.setError("Invalid email");
                        emailField.requestFocus();
                        break;

                    case "ERROR_OPERATION_NOT_ALLOWED":
                        Toast.makeText(getContext(), "This operation is not allowed. You must enable this service in the console.", Toast.LENGTH_LONG).show();
                        break;

                    case "ERROR_WEAK_PASSWORD":
                        Toast.makeText(getContext(), "The given password is invalid.", Toast.LENGTH_LONG).show();
                        passwordField.setError("The password is invalid it must 6 characters at least");
                        passwordField.requestFocus();
                        break;
                }
            }
        });
    }

}
