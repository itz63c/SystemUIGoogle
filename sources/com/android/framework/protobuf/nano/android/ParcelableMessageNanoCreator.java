package com.android.framework.protobuf.nano.android;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.android.framework.protobuf.nano.MessageNano;

public final class ParcelableMessageNanoCreator<T extends MessageNano> implements Creator<T> {
    static <T extends MessageNano> void writeToParcel(Class<T> cls, MessageNano messageNano, Parcel parcel) {
        parcel.writeString(cls.getName());
        parcel.writeByteArray(MessageNano.toByteArray(messageNano));
    }
}
