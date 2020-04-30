package com.google.protobuf.nano;

import java.nio.charset.Charset;

public final class InternalNano {
    public static final Object LAZY_INIT_LOCK = new Object();
    static final Charset UTF_8 = Charset.forName("UTF-8");

    static {
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
