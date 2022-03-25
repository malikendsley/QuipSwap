package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.schema.FriendRequest;

import java.util.ArrayList;

public interface RequestRetrieveListener {

    void onRequestsRetrieved(ArrayList<FriendRequest> requests);

    void onRequestsFailed(Exception e);

}
