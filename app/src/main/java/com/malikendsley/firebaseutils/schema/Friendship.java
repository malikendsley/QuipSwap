package com.malikendsley.firebaseutils.schema;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class Friendship {

    public String User1 = null;
    public String User2 = null;
    public Long timestamp = null;



    @SuppressWarnings("unused")
    public Friendship() {
        //necessary for firebase
    }

    public Friendship(String User1, String User2, Long timestamp) {
        this.User1 = User1;
        this.User2 = User2;
        this.timestamp = timestamp;
    }

    @PropertyName("User1")
    public String getUser1() {
        return User1;
    }

    @PropertyName("User2")
    public String getUser2() {
        return User2;
    }


    @NonNull
    public String toString() {
        return ("Friendship"+ "\nUser 1: " + User1 + " friends with " + User2);
    }
}
