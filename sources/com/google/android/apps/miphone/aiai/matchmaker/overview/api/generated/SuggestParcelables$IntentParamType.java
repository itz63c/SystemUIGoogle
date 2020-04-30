package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum SuggestParcelables$IntentParamType implements Parcelable {
    INTENT_PARAM_TYPE_UNKNOWN(0),
    INTENT_PARAM_TYPE_STRING(1),
    INTENT_PARAM_TYPE_INT(2),
    INTENT_PARAM_TYPE_FLOAT(3),
    INTENT_PARAM_TYPE_LONG(4),
    INTENT_PARAM_TYPE_INTENT(5),
    INTENT_PARAM_TYPE_CONTENT_URI(6);
    
    public static final Creator<SuggestParcelables$IntentParamType> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<SuggestParcelables$IntentParamType>() {
            public SuggestParcelables$IntentParamType createFromParcel(Parcel parcel) {
                return SuggestParcelables$IntentParamType.create(parcel);
            }

            public SuggestParcelables$IntentParamType[] newArray(int i) {
                return new SuggestParcelables$IntentParamType[i];
            }
        };
    }

    private SuggestParcelables$IntentParamType(int i) {
        this.value = i;
    }

    public static SuggestParcelables$IntentParamType create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static SuggestParcelables$IntentParamType create(int i) {
        if (i == 0) {
            return INTENT_PARAM_TYPE_UNKNOWN;
        }
        if (i == 1) {
            return INTENT_PARAM_TYPE_STRING;
        }
        if (i == 2) {
            return INTENT_PARAM_TYPE_INT;
        }
        if (i == 3) {
            return INTENT_PARAM_TYPE_FLOAT;
        }
        if (i == 4) {
            return INTENT_PARAM_TYPE_LONG;
        }
        if (i == 5) {
            return INTENT_PARAM_TYPE_INTENT;
        }
        if (i == 6) {
            return INTENT_PARAM_TYPE_CONTENT_URI;
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
