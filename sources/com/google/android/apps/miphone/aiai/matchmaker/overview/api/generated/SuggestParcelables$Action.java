package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SuggestParcelables$Action implements Parcelable {
    public static final Creator<SuggestParcelables$Action> CREATOR = new Creator<SuggestParcelables$Action>() {
        public SuggestParcelables$Action createFromParcel(Parcel parcel) {
            return new SuggestParcelables$Action(parcel);
        }

        public SuggestParcelables$Action[] newArray(int i) {
            return new SuggestParcelables$Action[i];
        }
    };
    public String dEPRECATEDIconBitmapId;
    public SuggestParcelables$IntentInfo dEPRECATEDIntentInfo;
    public String displayName;
    public String fullDisplayName;

    /* renamed from: id */
    public String f96id;
    public String opaquePayload;
    public SuggestParcelables$IntentInfo proxiedIntentInfo;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$Action(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$Action() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f96id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f96id);
        }
        if (this.displayName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.displayName);
        }
        if (this.dEPRECATEDIconBitmapId == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.dEPRECATEDIconBitmapId);
        }
        if (this.fullDisplayName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.fullDisplayName);
        }
        if (this.dEPRECATEDIntentInfo == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.dEPRECATEDIntentInfo.writeToParcel(parcel, i);
        }
        if (this.proxiedIntentInfo == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.proxiedIntentInfo.writeToParcel(parcel, i);
        }
        if (this.opaquePayload == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.opaquePayload);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.f96id = null;
        } else {
            this.f96id = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.displayName = null;
        } else {
            this.displayName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.dEPRECATEDIconBitmapId = null;
        } else {
            this.dEPRECATEDIconBitmapId = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.fullDisplayName = null;
        } else {
            this.fullDisplayName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.dEPRECATEDIntentInfo = null;
        } else {
            this.dEPRECATEDIntentInfo = (SuggestParcelables$IntentInfo) SuggestParcelables$IntentInfo.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.proxiedIntentInfo = null;
        } else {
            this.proxiedIntentInfo = (SuggestParcelables$IntentInfo) SuggestParcelables$IntentInfo.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.opaquePayload = null;
        } else {
            this.opaquePayload = parcel.readString();
        }
    }
}
