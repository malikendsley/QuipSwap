package com.malikendsley.firebaseutils.interfaces;

public interface QuipUploadListener {

    void onProgress(double progress);

    void onUploadComplete(String URI);

    void onUploadFail(Exception e);
}
