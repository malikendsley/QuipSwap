package com.malikendsley.firebaseutils.interfaces;

public interface RecentQuipListener {

    void onRetrieved(String URI);
    void onFailed(Exception e);
}
