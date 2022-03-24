package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.schema.Friendship;

import java.util.ArrayList;

public interface FriendRetrieveListener {

    void onFriendsRetrieved(ArrayList<Friendship> friendsList);

}
