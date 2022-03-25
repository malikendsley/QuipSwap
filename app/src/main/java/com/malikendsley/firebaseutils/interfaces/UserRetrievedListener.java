package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.schema.User;

public interface UserRetrievedListener {

    void onUserRetrieved(User user);

    void onRetrieveFailed(Exception e);

}
