package com.malikendsley.quipswap;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.FirebaseDatabase;
import com.malikendsley.firebaseutils.FirebaseHandler;
import com.malikendsley.firebaseutils.interfaces.RegisterUserListener;

public class SignupActivity extends AppCompatActivity {

    //private static final String TAG = "Own";
    FirebaseHandler mdb2 = new FirebaseHandler(FirebaseDatabase.getInstance().getReference(), this);
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


    void registerUser(String username, String email, String password) {
        mdb2.registerUser(username, email, password, new RegisterUserListener() {
            @Override
            public void onResult(String result) {
                if (result.equals("")) {
                    Toast.makeText(SignupActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, result, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDBFail(Exception e) {
                e.printStackTrace();
                Toast.makeText(SignupActivity.this, "Trouble signing up", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
