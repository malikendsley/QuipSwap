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

    public void setUID(String UID) {
        this.UID = UID;
    }

    //for the case where the UID isn't known at the time of creation
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public User(String UID, String username, String email) {
        this.UID = UID;
        this.username = username;
        this.email = email;
    }
}
