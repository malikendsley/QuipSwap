package com.malikendsley.utils.interfaces;

import android.graphics.Bitmap;

public interface RecentQuipListener {

    void onRetrieved(Bitmap bitmap);

    void onFailed(Exception e);

}
