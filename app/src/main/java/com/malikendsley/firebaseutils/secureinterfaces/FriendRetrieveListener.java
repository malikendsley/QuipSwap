package com.malikendsley.firebaseutils.secureinterfaces;

import java.util.ArrayList;

public interface FriendRetrieveListener {

    void onGetFriends(ArrayList<String> friendUIDList);
    void onGetFailed(Exception e);

}
