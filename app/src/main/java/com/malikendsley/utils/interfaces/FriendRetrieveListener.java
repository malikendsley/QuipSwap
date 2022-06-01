package com.malikendsley.utils.interfaces;

import java.util.ArrayList;

public interface FriendRetrieveListener {

    void onGetFriends(ArrayList<String> friendUIDList);

    void onGetFailed(Exception e);

}
