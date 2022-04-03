package com.malikendsley.firebaseutils.secureinterfaces;

import com.malikendsley.firebaseutils.schema.SharedQuip;
import com.malikendsley.firebaseutils.secureschema.PublicQuip;

import java.util.ArrayList;

public interface QuipRetrieveListener {

    void onRetrieveComplete(ArrayList<PublicQuip> quipList);

    void onRetrieveFail(Exception e);

}
