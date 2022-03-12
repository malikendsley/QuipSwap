package com.malikendsley.firebaseutils;

import com.google.firebase.database.PropertyName;

@SuppressWarnings("unused")
public class FriendRequest {

    private String Sender = "";
    private String Recipient = "";
    private boolean expandable = false;



    //necessary for firebase
    public FriendRequest(){

    }

    public FriendRequest(String Sender, String Recipient) {
        this.Sender = Sender;
        this.Recipient = Recipient;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
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
