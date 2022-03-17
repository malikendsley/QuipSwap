package com.malikendsley.firebaseutils;

public class Quip {

    String quipURI;
    String ownerUID;
    String timestamp;
    String quipID;

    @SuppressWarnings("unused")
    public Quip() {

    }

    public Quip(String quipURI, String ownerUID, String timestamp) {
        this.quipURI = quipURI;
        this.ownerUID = ownerUID;
        this.timestamp = timestamp;
    }

    //it appears the id of an element of the database may not be
    //immediately known upon its creation so an alternate
    //constructor is provided or convenience
    public Quip(String quipID, String quipURI, String ownerUID, String timestamp) {
        this.quipURI = quipURI;
        this.ownerUID = ownerUID;
        this.timestamp = timestamp;
        this.quipID = quipID;
    }

    public String toString() {
        return ("Quip owned by UID " + ownerUID + " created at " + timestamp);
    }
}
