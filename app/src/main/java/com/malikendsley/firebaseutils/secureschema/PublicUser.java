package com.malikendsley.firebaseutils.secureschema;

import com.google.firebase.database.PropertyName;

public class PublicUser {

    @PropertyName("Username")
    private final String Username;
    @PropertyName("Key")
    private String Key;

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
