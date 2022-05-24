package com.malikendsley.firebaseutils.secureschema;

import com.google.firebase.database.PropertyName;

public class PrivateUser {

    @PropertyName("Email")
    private final String Email;
    @PropertyName("Username")
    private final String Username;
    @PropertyName("Key")
    private String Key;

    @SuppressWarnings("unused")
    //necessary for firebase
    public PrivateUser() {
        Email = "unset";
        Username = "unset";
    }

    public PrivateUser(String email, String username) {
        Email = email;
        Username = username;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }

    @PropertyName("Email")
    public String getEmail() {
        return Email;
    }

    @PropertyName("Username")
    public String getUsername() {
        return Username;
    }
}
