package com.malikendsley.firebaseutils;


//system intended to make interfacing with Firebase standardized and concise
//these functions perform no checks on their inputs, providing properly formatted data
//is the responsibility of the caller
import android.net.wifi.hotspot2.pps.Credential;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class FirebaseUtils {

    /*declaring and using these here may make tracking a users
      status accurately difficult each function will accept the values derived
    from these variables directly until this is verified
    */
    FirebaseAuth mAuth;
    FirebaseUser user;
    boolean signedIn = false;
    private DatabaseReference mDatabase;

    public FirebaseUtils(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if(user != null){
            signedIn = true;
        }

    }


    //TODO: Implement these, then convert them to cloud functions (need to learn how)
    //Store a Quip
    /*


     */
    public int createUser(String UID, String name, String email){
        //TODO prevent duplicate usernames somehow
        User user = new User(name, email);

        mDatabase.child("Users").child(UID).setValue(user);

        return 0;
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
