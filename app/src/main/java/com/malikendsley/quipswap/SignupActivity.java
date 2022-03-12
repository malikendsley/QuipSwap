package com.malikendsley.quipswap;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.User;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Own";

    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private EditText username;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        username = findViewById(R.id.signUpUsername);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signUpPassword);
        Button registerButton = findViewById(R.id.registerButton);


        registerButton.setOnClickListener(v -> {
            String txt_username = username.getText().toString();
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            //rudimentary validation, can use onTextChanged() later
            if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password) || TextUtils.isEmpty(txt_username)) {
                Toast.makeText(this, "Missing Information", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            } else {
                //register user
                registerUser(txt_username, txt_email, txt_password);
            }
        });
    }

    private void registerUser(String username, String email, String password) {
        //prevent duplicate usernames
        mDatabase.child("TakenUsernames").child(username).get().addOnCompleteListener(noDupTask -> {
            if (!noDupTask.isSuccessful()) {
                Log.e(TAG, "Error getting data", noDupTask.getException());
                Toast.makeText(this, "SignupActivity: Database Error", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Data: " + noDupTask.getResult().getValue());
                //null = username is free
                if (noDupTask.getResult().getValue() == null) {
                    //try create user
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignupActivity.this, regTask -> {
                        if (regTask.isSuccessful()) {
                            Log.i(TAG, "SignupActivity: User registered");
                            Toast.makeText(SignupActivity.this, "SignupActivity: Registration Successful", Toast.LENGTH_SHORT).show();
                            //create record + index
                            User user = new User(username, email);
                            mDatabase.child("Users").child(Objects.requireNonNull(mAuth.getUid())).setValue(user).addOnCompleteListener(recordTask -> {
                                if (recordTask.isSuccessful()) {
                                    Log.i(TAG, "Write Successful");
                                    //index can probably be handled via a cloud function later on, will reduce complexity
                                    mDatabase.child("TakenUsernames").child(username).setValue(mAuth.getUid()).addOnCompleteListener(indexTask -> {
                                        if (indexTask.isSuccessful()) {
                                            Log.i(TAG, "Index Update Successful");
                                            //all database work is done, go home
                                            startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Log.i(TAG, "Index Failed");
                                            Toast.makeText(SignupActivity.this, "SignupActivity: Permission Denied", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    //write failed (unlikely)
                                    Log.i(TAG, "Write Failed");
                                    Toast.makeText(SignupActivity.this, "SignupActivity: Permission Denied", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            String code = ((FirebaseAuthException) Objects.requireNonNull(regTask.getException())).getErrorCode();
                            Log.i(TAG, "SignupActivity: CreateUserWith failed");
                            Toast.makeText(SignupActivity.this, code, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    //username taken
                    Log.i(TAG, "SignupActivity: Username Taken");
                    Toast.makeText(SignupActivity.this, "SignupActivity: Username taken", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
