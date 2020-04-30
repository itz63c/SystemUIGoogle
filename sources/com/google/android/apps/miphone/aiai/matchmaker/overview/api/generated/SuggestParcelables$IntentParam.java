package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SuggestParcelables$IntentParam implements Parcelable {
    public static final Creator<SuggestParcelables$IntentParam> CREATOR = new Creator<SuggestParcelables$IntentParam>() {
        public SuggestParcelables$IntentParam createFromParcel(Parcel parcel) {
            return new SuggestParcelables$IntentParam(parcel);
        }

        public SuggestParcelables$IntentParam[] newArray(int i) {
            return new SuggestParcelables$IntentParam[i];
        }
    };
    public String contentUri;
    public float floatValue;
    public int intValue;
    public SuggestParcelables$IntentInfo intentValue;
    public long longValue;
    public String name;
    public String strValue;
    public SuggestParcelables$IntentParamType type;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$IntentParam(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$IntentParam() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.name == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.name);
        }
        if (this.type == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.type.writeToParcel(parcel, i);
        }
        if (this.strValue == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.strValue);
        }
        parcel.writeInt(this.intValue);
        parcel.writeFloat(this.floatValue);
        parcel.writeLong(this.longValue);
        if (this.intentValue == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.intentValue.writeToParcel(parcel, i);
        }
        if (this.contentUri == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.contentUri);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.name = null;
        } else {
            this.name = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.type = null;
        } else {
            this.type = (SuggestParcelables$IntentParamType) SuggestParcelables$IntentParamType.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.strValue = null;
        } else {
            this.strValue = parcel.readString();
        }
        this.intValue = parcel.readInt();
        this.floatValue = parcel.readFloat();
        this.longValue = parcel.readLong();
        if (parcel.readByte() == 0) {
            this.intentValue = null;
        } else {
            this.intentValue = (SuggestParcelables$IntentInfo) SuggestParcelables$IntentInfo.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.contentUri = null;
        } else {
            this.contentUri = parcel.readString();
        }
    }
}
