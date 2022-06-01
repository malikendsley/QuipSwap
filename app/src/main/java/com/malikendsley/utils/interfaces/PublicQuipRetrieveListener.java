package com.malikendsley.utils.interfaces;

import com.malikendsley.utils.schema.PublicQuip;

import java.util.ArrayList;

public interface PublicQuipRetrieveListener {

    void onRetrieveComplete(ArrayList<PublicQuip> quipList);

    void onRetrieveFail(Exception e);

}
