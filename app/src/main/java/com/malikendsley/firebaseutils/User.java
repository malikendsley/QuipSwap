package com.malikendsley.firebaseutils;

public class User {

    private String username;
    private String email;
    private String UID;

    @SuppressWarnings("unused")
    public User() {
        username = "unset";
        email = "unset";
        UID = "unset";
        //necessary for firebase
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getUID() {
        return UID;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    //it appears the id of an element of the database may not be
    //immediately known upon its creation so an alternate
    //constructor is provided or convenience
    public User(String UID, String username, String email) {
        this.UID = UID;
        this.username = username;
        this.email = email;
    }
}
