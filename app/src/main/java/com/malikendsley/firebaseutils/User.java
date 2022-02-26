package com.malikendsley.firebaseutils;

public class User {

    public String username;
    public String email;


    @SuppressWarnings("unused")
    public User(){
        //necessary for firebase
    }

    public User(String username, String email){
        this.username = username;
        this.email = email;
    }
}
