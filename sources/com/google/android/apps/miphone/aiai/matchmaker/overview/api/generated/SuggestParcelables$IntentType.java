package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum SuggestParcelables$IntentType implements Parcelable {
    DEFAULT(0),
    COPY_TEXT(1),
    SHARE_IMAGE(2),
    LENS(3);
    
    public static final Creator<SuggestParcelables$IntentType> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<SuggestParcelables$IntentType>() {
            public SuggestParcelables$IntentType createFromParcel(Parcel parcel) {
                return SuggestParcelables$IntentType.create(parcel);
            }

            public SuggestParcelables$IntentType[] newArray(int i) {
                return new SuggestParcelables$IntentType[i];
            }
        };
    }

    private SuggestParcelables$IntentType(int i) {
        this.value = i;
    }

    public static SuggestParcelables$IntentType create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static SuggestParcelables$IntentType create(int i) {
        if (i == 0) {
            return DEFAULT;
        }
        if (i == 1) {
            return COPY_TEXT;
        }
        if (i == 2) {
            return SHARE_IMAGE;
        }
        if (i == 3) {
            return LENS;
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
