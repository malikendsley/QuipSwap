package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.SharedQuip;

import java.util.ArrayList;

public interface QuipRetrieveListener {

    void onRetrieveComplete(ArrayList<SharedQuip> quips);

    void onRetrieveFail(Exception e);

}
