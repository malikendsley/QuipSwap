package com.malikendsley.firebaseutils.secureinterfaces;

import com.malikendsley.firebaseutils.secureschema.PrivateQuip;

public interface PrivateQuipRetrievedListener {

    void onRetrieveComplete(PrivateQuip quip);

    void onRetrieveFail(Exception e);
}
