package com.malikendsley.firebaseutils;

@SuppressWarnings("unused")
public class FriendRequest {

    private String sender;
    private String recipient;

    //necessary for firebase
    public FriendRequest(){

    }

    public FriendRequest(String sender, String recipient) {
        this.sender = sender;
        this.recipient = recipient;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }
}
