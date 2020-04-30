package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SuggestParcelables$ContentRect implements Parcelable {
    public static final Creator<SuggestParcelables$ContentRect> CREATOR = new Creator<SuggestParcelables$ContentRect>() {
        public SuggestParcelables$ContentRect createFromParcel(Parcel parcel) {
            return new SuggestParcelables$ContentRect(parcel);
        }

        public SuggestParcelables$ContentRect[] newArray(int i) {
            return new SuggestParcelables$ContentRect[i];
        }
    };
    public int contentGroupIndex;
    public SuggestParcelables$ContentType contentType;
    public String contentUri;
    public int lineId;
    public SuggestParcelables$OnScreenRect rect;
    public String text;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$ContentRect(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$ContentRect() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.rect == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.rect.writeToParcel(parcel, i);
        }
        if (this.text == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.text);
        }
        if (this.contentType == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.contentType.writeToParcel(parcel, i);
        }
        parcel.writeInt(this.lineId);
        if (this.contentUri == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.contentUri);
        }
        parcel.writeInt(this.contentGroupIndex);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.rect = null;
        } else {
            this.rect = (SuggestParcelables$OnScreenRect) SuggestParcelables$OnScreenRect.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.text = null;
        } else {
            this.text = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.contentType = null;
        } else {
            this.contentType = (SuggestParcelables$ContentType) SuggestParcelables$ContentType.CREATOR.createFromParcel(parcel);
        }
        this.lineId = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.contentUri = null;
        } else {
            this.contentUri = parcel.readString();
        }
        this.contentGroupIndex = parcel.readInt();
    }
}
