package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.secureschema.PublicQuip;

import java.util.ArrayList;

public interface PublicQuipRetrieveListener {

    void onRetrieveComplete(ArrayList<PublicQuip> quipList);

    void onRetrieveFail(Exception e);

}
