package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SuggestParcelables$ExtrasInfo implements Parcelable {
    public static final Creator<SuggestParcelables$ExtrasInfo> CREATOR = new Creator<SuggestParcelables$ExtrasInfo>() {
        public SuggestParcelables$ExtrasInfo createFromParcel(Parcel parcel) {
            return new SuggestParcelables$ExtrasInfo(parcel);
        }

        public SuggestParcelables$ExtrasInfo[] newArray(int i) {
            return new SuggestParcelables$ExtrasInfo[i];
        }
    };
    public boolean containsBitmaps;
    public boolean containsPendingIntents;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$ExtrasInfo(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$ExtrasInfo() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.containsPendingIntents) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.containsBitmaps) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
    }

    private void readFromParcel(Parcel parcel) {
        boolean z = false;
        this.containsPendingIntents = parcel.readByte() == 1;
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.containsBitmaps = z;
    }
}
