package com.malikendsley.utils.interfaces;

public interface RegisterUserListener {

    void onResult(String result);

    void onDBFail(Exception e);

}
