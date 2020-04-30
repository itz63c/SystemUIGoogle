package com.android.framework.protobuf.nano;

import java.nio.charset.Charset;

public final class InternalNano {
    static {
        Charset.forName("UTF-8");
        Charset.forName("ISO-8859-1");
    }

    public static void cloneUnknownFieldData(ExtendableMessageNano extendableMessageNano, ExtendableMessageNano extendableMessageNano2) {
        FieldArray fieldArray = extendableMessageNano.unknownFieldData;
        if (fieldArray != null) {
            fieldArray.clone();
            throw null;
        }
    }
}
