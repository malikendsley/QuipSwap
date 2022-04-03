package com.malikendsley.firebaseutils.secureinterfaces;

public interface FriendAddListener {

    void onResult(String result);

    void onDatabaseException(Exception e);
}
