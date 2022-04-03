package com.malikendsley.firebaseutils.secureinterfaces;

import com.malikendsley.firebaseutils.secureschema.PrivateUser;

public interface UserRetrievedListener {

    void onUserRetrieved(PrivateUser user);

    void onRetrieveFailed(Exception e);

}
