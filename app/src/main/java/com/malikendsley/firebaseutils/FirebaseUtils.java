package com.malikendsley.firebaseutils;


//system intended to make interfacing with Firebase standardized and concise
//these functions perform no checks on their inputs, providing properly formatted data
//is the responsibility of the caller

import android.content.Context;
import android.util.Log;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FirebaseUtils {

    /*declaring and using these here may make tracking a users
      status accurately difficult each function will accept the values derived
    from these variables directly until this is verified
    */
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    boolean signedIn = false;
    private final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private static final String TAG = "Own";
    Context context;
    public FirebaseUtils(Context context){
        if(user != null){
            signedIn = true;
        }
        this.context = context;
    }


    //TODO: Implement these, then convert them to cloud functions (need to learn how)
    //Store a Quip
    /*


     */
    public void createUser(String UID, String username, String email){
        mDatabase.child("TakenUsernames").child(username).get().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Log.e(TAG, "Error getting data", task.getException());
            } else {
                Log.d(TAG, "Data: " + task.getResult().getValue());
                if(task.getResult().getValue() == null){
                    Log.i(TAG, "Username " + username + " available");

                    User user = new User(username, email);
                    //create record + index
                    mDatabase.child("Users").child(UID).setValue(user).addOnSuccessListener(unused -> Log.i(TAG, "Write Successful"));
                    mDatabase.child("TakenUsernames").child(username).setValue(UID).addOnSuccessListener(unused -> Log.i(TAG, "Index Update Successful"));

                } else {
                    Log.i(TAG, "Username " + username + " taken");
                    Toast.makeText(context, "Username Already Taken", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Share a Quip
    /*


     */
    //Retrieve your Quips
    /*


     */
    //Retrieve shared Quips
    /*


     */
    //Retrieve Friends
    /*


     */
    //Load User
    /*


     */
    //Load Quip
    /*


    */
}
