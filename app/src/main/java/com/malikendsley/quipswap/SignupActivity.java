package com.malikendsley.quipswap;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.schema.User;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "Own";
    //TODO migrate
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    FirebaseHandler mdb = new FirebaseHandler(mDatabase);
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private EditText username;
    private EditText email;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        username = findViewById(R.id.signUpUsername);
        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signUpPassword);
        Button registerButton = findViewById(R.id.registerButton);


        password.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                trySubmit();

                return true;
            } else {
                return false;
            }
        });

        registerButton.setOnClickListener(v -> trySubmit());
    }

    private void trySubmit() {
        String txt_username = username.getText().toString();
        String txt_email = email.getText().toString();
        String txt_password = password.getText().toString();

        if (validateUser(txt_username, txt_email, txt_password)) {
            registerUser(txt_username, txt_email, txt_password);
        }
    }

    private boolean validateUser(String username, String email, String password) {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Missing Information", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private void registerUser(String username, String email, String password) {
        //prevent duplicate usernames
        mdb.resolveUsername(username, resolvedUID -> {
            if (resolvedUID == null) {
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
        });
    }
}
