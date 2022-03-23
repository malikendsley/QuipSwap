package com.malikendsley.firebaseutils;

@SuppressWarnings("unused")

public class SharedQuip {

    String Sender;
    String Recipient;
    String URI;

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getRecipient() {
        return Recipient;
    }

    public void setRecipient(String recipient) {
        Recipient = recipient;
    }

    public String getURI() {
        return URI;
    }

    public void setSharedQuipID(String URI) {
        this.URI = URI;
    }

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
    public SharedQuip(String URI, String sender, String recipient) {
        this.Sender = sender;
        this.Recipient = recipient;
        this.URI = URI;
    }
}
