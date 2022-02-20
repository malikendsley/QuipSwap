package com.malikendsley.quipswap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email = findViewById(R.id.signupEmail);
        password = findViewById(R.id.signUpPassword);
        signupButton = findViewById(R.id.signUpButton);


        signupButton.setOnClickListener(v -> {
            String txt_email = email.getText().toString();
            String txt_password = password.getText().toString();
            //rudimentary validation, can use onTextChanged() later
            if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)){
                Toast.makeText(SignUpActivity.this, "Missing Information", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                Toast.makeText(SignUpActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            } else {
                //register user
                Toast.makeText(SignUpActivity.this, "Register User", Toast.LENGTH_LONG).show();
            }
        });
    }


}