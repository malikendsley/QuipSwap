package com.malikendsley.utils.schema;

import com.google.firebase.database.PropertyName;

public class PublicUser {

    @PropertyName("Username")
    private final String Username;
    @PropertyName("Key")
    private String Key;

    @SuppressWarnings("unused")
    public PublicUser() {
        Username = "unset";
        Key = "unset";
    }

    public PublicUser(String username) {
        this.Username = username;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    @PropertyName("Username")
    public String getUsername() {
        return Username;
    }

}
