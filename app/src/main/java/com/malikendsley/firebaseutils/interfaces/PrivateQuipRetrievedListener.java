package com.malikendsley.firebaseutils.interfaces;

import com.malikendsley.firebaseutils.secureschema.PrivateQuip;

public interface PrivateQuipRetrievedListener {

    void onRetrieveComplete(PrivateQuip quip);

    void onRetrieveFail(Exception e);

}
