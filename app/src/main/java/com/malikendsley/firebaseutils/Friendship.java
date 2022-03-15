package com.malikendsley.firebaseutils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Friendship {

    private final String User1 = null;
    private final String User2 = null;
    private final String timestamp = null;
    private String key = null;


    @SuppressWarnings("unused")
    public Friendship() {
        //necessary for firebase
    }

    public String getUser1() {
        return User1;
    }

    public String getUser2() {
        return User2;
    }

    public String getKey() {
        return key;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String toString() {
        return ("Friendship w/ key " + key + "\nUser 1: " + User1 + " friends with " + User2);
    }
}
