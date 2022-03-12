package com.malikendsley.firebaseutils;


public class SharedQuip {

    String User1;
    String User2;
    String sharedQuipID;

    @SuppressWarnings("unused")
    public SharedQuip() {
        //necessary for firebase
    }

    public SharedQuip(String User1, String User2) {
        this.User1 = User1;
        this.User2 = User2;
    }

    //it appears the id of an element of the database may not be
    //immediately known upon its creation so an alternate
    //constructor is provided or convenience
    public SharedQuip(String sharedQuipID, String User1, String User2) {
        this.User1 = User1;
        this.User2 = User2;
        this.sharedQuipID = sharedQuipID;
    }
}
