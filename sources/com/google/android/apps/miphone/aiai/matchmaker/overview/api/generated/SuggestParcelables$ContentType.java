package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum SuggestParcelables$ContentType implements Parcelable {
    CONTENT_TYPE_UNKNOWN(0),
    CONTENT_TYPE_TEXT(1),
    CONTENT_TYPE_IMAGE(2);
    
    public static final Creator<SuggestParcelables$ContentType> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<SuggestParcelables$ContentType>() {
            public SuggestParcelables$ContentType createFromParcel(Parcel parcel) {
                return SuggestParcelables$ContentType.create(parcel);
            }

            public SuggestParcelables$ContentType[] newArray(int i) {
                return new SuggestParcelables$ContentType[i];
            }
        };
    }

    private SuggestParcelables$ContentType(int i) {
        this.value = i;
    }

    public static SuggestParcelables$ContentType create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static SuggestParcelables$ContentType create(int i) {
        if (i == 0) {
            return CONTENT_TYPE_UNKNOWN;
        }
        if (i == 1) {
            return CONTENT_TYPE_TEXT;
        }
        if (i == 2) {
            return CONTENT_TYPE_IMAGE;
        }
        StringBuilder sb = new StringBuilder(26);
        sb.append("Invalid value: ");
        sb.append(i);
        throw new RuntimeException(sb.toString());
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.value);
    }
}
