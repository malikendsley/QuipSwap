package com.malikendsley.firebaseutils;

public class Friendship {

    private String User1;
    private String User2;
    private String key;

    @SuppressWarnings("unused")
    public Friendship(){

    }

    public Friendship(String User1, String User2){
        this.User1 = User1;
        this.User2 = User2;
    }

    public void setKey(String key){
        this.key = key;
    }

    public String toString(){
        return("Friendship " + key + ": " + User1 + " - " + User2);
    }
}
