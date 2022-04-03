package com.malikendsley.firebaseutils.secureschema;

import com.google.firebase.database.PropertyName;

public class PublicUser {

    @PropertyName("Username")
    private final String username;

    public PublicUser(String username) {
        this.username = username;
    }

    @PropertyName("Username")
    public String getUsername(){
        return username;
    }

}
