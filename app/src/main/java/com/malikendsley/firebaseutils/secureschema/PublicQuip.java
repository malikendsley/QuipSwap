package com.malikendsley.firebaseutils.secureschema;

import com.google.firebase.database.PropertyName;

public class PublicQuip implements Comparable<PublicQuip> {

    @PropertyName("Sender")
    private final String Sender;

    @PropertyName("Recipient")
    private final String Recipient;

    @PropertyName("Timestamp")
    private final long Timestamp;

    @PropertyName("Key")
    private String Key;

    public PublicQuip(String sender, String recipient, long timestamp) {
        Sender = sender;
        Recipient = recipient;
        Timestamp = timestamp;
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
        return Timestamp;
    }

    @PropertyName("Key")
    public String getKey() {
        return Key;
    }

    @Override
    public int compareTo(PublicQuip otherQuip) {
        //"max" sharedQuip should be soonest, with the highest timestamp
        return (int) (Timestamp - otherQuip.Timestamp);
    }

    public void setKey(String key) {
        Key = key;
    }
}
