package com.malikendsley.firebaseutils;


public class SharedQuip {

    String Sender;
    String Recipient;
    String sharedQuipID;

    @SuppressWarnings("unused")
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
    public SharedQuip(String sharedQuipID, String sender, String recipient) {
        this.Sender = sender;
        this.Recipient = recipient;
        this.sharedQuipID = sharedQuipID;
    }
}
