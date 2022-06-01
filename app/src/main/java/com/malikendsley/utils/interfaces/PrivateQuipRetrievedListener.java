package com.malikendsley.utils.interfaces;

import com.malikendsley.utils.schema.PrivateQuip;

public interface PrivateQuipRetrievedListener {

    void onRetrieveComplete(PrivateQuip quip);

    void onRetrieveFail(Exception e);

}
