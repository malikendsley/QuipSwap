package com.malikendsley.firebaseutils;

public class User {

    public String username;
    public String email;
    public String UID;

    @SuppressWarnings("unused")
    public User(){
        //necessary for firebase
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }

    //it appears the id of an element of the database may not be
    //immediately known upon its creation so an alternate
    //constructor is provided or convenience
    public User(String UID, String username, String email){
        this.UID = UID;
        this.username = username;
        this.email = email;
    }
}
