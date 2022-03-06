package com.malikendsley.firebaseutils;

import com.google.firebase.database.PropertyName;

@SuppressWarnings("unused")
public class FriendRequest {

    private String Sender = "";
    private String Recipient = "";

    //necessary for firebase
    public FriendRequest(){

    }

    public FriendRequest(String Sender, String Recipient) {
        this.Sender = Sender;
        this.Recipient = Recipient;
    }
    @PropertyName("Sender")
    public String getSender() {
        return Sender;
    }
    @PropertyName("Recipient")
    public String getRecipient() {
        return Recipient;
    }
}
