package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum SuggestParcelables$InteractionType implements Parcelable {
    UNKNOWN(0),
    LONG_PRESS(1),
    GLEAM(2),
    CHIP(3),
    GLEAM_CHIP(4),
    SCREENSHOT_NOTIFICATION(5);
    
    public static final Creator<SuggestParcelables$InteractionType> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<SuggestParcelables$InteractionType>() {
            public SuggestParcelables$InteractionType createFromParcel(Parcel parcel) {
                return SuggestParcelables$InteractionType.create(parcel);
            }

            public SuggestParcelables$InteractionType[] newArray(int i) {
                return new SuggestParcelables$InteractionType[i];
            }
        };
    }

    private SuggestParcelables$InteractionType(int i) {
        this.value = i;
    }

    public static SuggestParcelables$InteractionType create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static SuggestParcelables$InteractionType create(int i) {
        if (i == 0) {
            return UNKNOWN;
        }
        if (i == 1) {
            return LONG_PRESS;
        }
        if (i == 2) {
            return GLEAM;
        }
        if (i == 3) {
            return CHIP;
        }
        if (i == 4) {
            return GLEAM_CHIP;
        }
        if (i == 5) {
            return SCREENSHOT_NOTIFICATION;
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
