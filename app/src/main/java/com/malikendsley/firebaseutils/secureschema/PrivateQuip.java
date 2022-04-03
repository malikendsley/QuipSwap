package com.malikendsley.firebaseutils.secureschema;

import com.google.firebase.database.PropertyName;

public class PrivateQuip {
    @PropertyName("Sender")
    private final String Sender;

    @PropertyName("Recipient")
    private final String Recipient;

    @PropertyName("Timestamp")
    private final long timestamp;

    @PropertyName("URI")
    private final String URI;

    @PropertyName("Key")
    private String Key;

    public PrivateQuip(String sender, String recipient, long timestamp, String URI) {
        Sender = sender;
        Recipient = recipient;
        this.timestamp = timestamp;
        this.URI = URI;
    }

    @PropertyName("Sender")
    public String getSender() {
        return Sender;
    }

    @PropertyName("Recipient")
    public String getRecipient() {
        return Recipient;
    }

    @PropertyName("Timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @PropertyName("URI")
    public String getURI() {
        return URI;
    }

    @PropertyName("Key")
    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
