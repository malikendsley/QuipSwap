package com.malikendsley.quipswap;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class ShareQuipActivity extends AppCompatActivity {

    String quipPath;
    static final String TAG = "Own";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_quip);


        quipPath = getIntent().getStringExtra("Path");
        Log.i(TAG, "Image path retrieved: " + quipPath);
    }
}