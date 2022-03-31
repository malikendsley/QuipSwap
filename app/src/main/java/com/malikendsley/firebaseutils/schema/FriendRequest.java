package com.malikendsley.firebaseutils.schema;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

@SuppressWarnings("unused")
public class FriendRequest {

    private String Sender = "unset";
    private String Recipient = "unset";
    private String key = "unset";
    @Exclude
    private boolean expandable = false;


    //necessary for firebase
    public FriendRequest() {

    }

    public FriendRequest(String Sender, String Recipient) {
        this.Sender = Sender;
        this.Recipient = Recipient;
    }

    @Exclude
    public boolean isExpandable() {
        return expandable;
    }

    @Exclude
    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
