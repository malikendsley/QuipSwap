package com.malikendsley.firebaseutils.interfaces;

public interface RegisterUserListener {

    void onResult(String result);
    void onDBFail(Exception e);

}
