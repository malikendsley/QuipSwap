package com.malikendsley.firebaseutils.interfaces;

import android.graphics.Bitmap;

public interface RecentQuipListener {

    void onRetrieved(Bitmap bitmap);

    void onFailed(Exception e);

}
