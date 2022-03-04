package com.malikendsley.firebaseutils;

import android.app.Application;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHandler extends Application {
    private static final String TAG = "Own";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Persistence Enabled");
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
