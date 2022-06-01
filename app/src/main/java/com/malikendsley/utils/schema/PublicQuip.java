package com.malikendsley.utils.schema;

import com.google.firebase.database.PropertyName;

public class PublicQuip implements Comparable<PublicQuip> {

    @PropertyName("Timestamp")
    private final long Timestamp;
    @PropertyName("Sender")
    private String Sender = "unset";
    @PropertyName("Recipient")
    private String Recipient = "unset";
    @PropertyName("Key")
    private String Key;

    @SuppressWarnings("unused")
    public PublicQuip() {
        Timestamp = -1;
    }

    public PublicQuip(String sender, String recipient, long timestamp) {
        Sender = sender;
        Recipient = recipient;
        Timestamp = timestamp;
    }

    @PropertyName("Sender")
    public String getSender() {
        return this.Sender;
    }

    @PropertyName("Recipient")
    public String getRecipient() {
        return this.Recipient;
    }

    @PropertyName("Timestamp")
    public long getTimestamp() {
        return Timestamp;
    }

    @PropertyName("Key")
    public String getKey() {
        return Key;
    }

    @PropertyName("Key")
    public void setKey(String key) {
        this.Key = key;
    }

    @Override
    public int compareTo(PublicQuip otherQuip) {
        //"max" sharedQuip should be soonest, with the highest timestamp
        return (int) (Timestamp - otherQuip.Timestamp);
    }
}
