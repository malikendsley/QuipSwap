package com.malikendsley.firebaseutils;

public class Quip {

    String quipURI;
    String localURI;
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

    public Quip(String quipID, String quipURI, String ownerUID, String timestamp) {
        this.quipURI = quipURI;
        this.ownerUID = ownerUID;
        this.timestamp = timestamp;
        this.quipID = quipID;
    }

    public String toString() {
        return ("Quip owned by " + ownerUID + " created on " + timestamp);
    }
}
