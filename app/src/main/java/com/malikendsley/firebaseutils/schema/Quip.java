package com.malikendsley.firebaseutils.schema;

import androidx.annotation.NonNull;

import com.google.firebase.database.Exclude;
@SuppressWarnings("unused")

public class Quip {

    String quipURI;
    @Exclude
    String localURI;
    String ownerUID;
    String timestamp;
    String quipID;

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

    public String getQuipURI() {
        return quipURI;
    }

    public void setQuipURI(String quipURI) {
        this.quipURI = quipURI;
    }

    public String getOwnerUID() {
        return ownerUID;
    }

    public void setOwnerUID(String ownerUID) {
        this.ownerUID = ownerUID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuipID() {
        return quipID;
    }

    public void setQuipID(String quipID) {
        this.quipID = quipID;
    }

    public String getLocalURI(String localURI) {
        return localURI;
    }

    public void setLocalURI(String localURI) {
        this.localURI = localURI;
    }

    @NonNull
    public String toString() {
        return ("Quip owned by " + ownerUID + " created on " + timestamp);
    }
}
