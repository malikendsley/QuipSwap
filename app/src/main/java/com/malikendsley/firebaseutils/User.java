package com.malikendsley.firebaseutils;


import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

//Intended to be supplied to FirebaseUtils as what will become a document
@IgnoreExtraProperties
public class User {

    private static final String TAG = "Own";
    public String username;
    public String email;

    public User(){
        Log.i(TAG, "User.java / User Created: " + username);
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }
}
