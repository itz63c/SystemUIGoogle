package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SuggestParcelables$OnScreenRect implements Parcelable {
    public static final Creator<SuggestParcelables$OnScreenRect> CREATOR = new Creator<SuggestParcelables$OnScreenRect>() {
        public SuggestParcelables$OnScreenRect createFromParcel(Parcel parcel) {
            return new SuggestParcelables$OnScreenRect(parcel);
        }

        public SuggestParcelables$OnScreenRect[] newArray(int i) {
            return new SuggestParcelables$OnScreenRect[i];
        }
    };
    public float height;
    public float left;
    public float top;
    public float width;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$OnScreenRect(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$OnScreenRect() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.left);
        parcel.writeFloat(this.top);
        parcel.writeFloat(this.width);
        parcel.writeFloat(this.height);
    }

    private void readFromParcel(Parcel parcel) {
        this.left = parcel.readFloat();
        this.top = parcel.readFloat();
        this.width = parcel.readFloat();
        this.height = parcel.readFloat();
    }
}
