package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.schema.SharedQuip;

import java.util.ArrayList;

public interface QuipRetrieveListener {

    void onRetrieveComplete(ArrayList<SharedQuip> quips);

    void onRetrieveFail(Exception e);

}
