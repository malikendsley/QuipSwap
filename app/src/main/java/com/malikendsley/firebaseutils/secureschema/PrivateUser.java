package com.malikendsley.firebaseutils.secureschema;

import com.google.firebase.database.PropertyName;

public class PrivateUser {

    @PropertyName("Email")
    private final String email;
    @PropertyName("Username")
    private final String username;

    public PrivateUser(String email, String username) {
        this.email = email;
        this.username = username;
    }

    @PropertyName("Email")
    public String getEmail() {
        return email;
    }

    @PropertyName("Username")
    public String getUsername() {
        return username;
    }
}
