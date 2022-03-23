package com.malikendsley.firebaseutils.interfaces;

public interface QuipUploadListener {

    void onUploadComplete(String URI);
    void onUploadFail(Exception e);
}
