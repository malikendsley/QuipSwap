package com.malikendsley.firebaseutils.schema;

import com.google.firebase.database.PropertyName;

public class User {

    public final String Username;
    public final String Email;
    private String UID;

    @SuppressWarnings("unused")
    public User() {
        Username = "unset";
        Email = "unset";
        UID = "unset";
        //necessary for firebase
    }
    @PropertyName("Username")
    public String getUsername() {
        return Username;
    }
    @PropertyName("Email")
    public String getEmail() {
        return Email;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    //for the case where the UID isn't known at the time of creation
    public User(String username, String email) {
        this.Username = username;
        this.Email = email;
    }

}
