package com.malikendsley.utils.interfaces;

import java.util.ArrayList;

public interface GetRequestsListener {

    void onRequests(ArrayList<String> requestList);

    void onGetFail(Exception e);

}
