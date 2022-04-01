package com.malikendsley.firebaseutils.schema;

import androidx.annotation.NonNull;

import com.google.firebase.database.PropertyName;

@SuppressWarnings("unused")

public class SharedQuip implements Comparable<SharedQuip>{

    @PropertyName("Sender")
    public String Sender;
    @PropertyName("Recipient")
    public String Recipient;
    @PropertyName("URI")
    public String URI;
    @PropertyName("QID")
    public String QID;
    @PropertyName("Timestamp")
    public long Timestamp;

    public SharedQuip() {
        //necessary for firebase
    }

    public SharedQuip(String sender, String recipient) {
        this.Sender = sender;
        this.Recipient = recipient;
    }

    //it appears the id of an element of the database may not be
    //immediately known upon its creation so an alternate
    //constructor is provided or convenience
    public SharedQuip(String URI, String sender, String recipient, long timestamp) {
        this.Sender = sender;
        this.Recipient = recipient;
        this.URI = URI;
        this.Timestamp = timestamp;
    }

    @NonNull
    public String toString() {
        return Sender + " " + Recipient + " " + URI;
    }


    @Override
    public int compareTo(SharedQuip sharedQuip) {
        //"max" sharedQuip should be soonest, with the highest timestamp
        return (int) (Timestamp - sharedQuip.Timestamp);
    }
}
