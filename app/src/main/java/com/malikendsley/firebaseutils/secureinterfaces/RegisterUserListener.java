package com.malikendsley.firebaseutils.secureinterfaces;

public interface RegisterUserListener {

    void onResult(String result);
    void onDBFail(Exception e);

}
