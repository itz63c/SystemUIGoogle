package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class SuggestParcelables$IntentInfo implements Parcelable {
    public static final Creator<SuggestParcelables$IntentInfo> CREATOR = new Creator<SuggestParcelables$IntentInfo>() {
        public SuggestParcelables$IntentInfo createFromParcel(Parcel parcel) {
            return new SuggestParcelables$IntentInfo(parcel);
        }

        public SuggestParcelables$IntentInfo[] newArray(int i) {
            return new SuggestParcelables$IntentInfo[i];
        }
    };
    public String action;
    public String className;
    public int flags;
    public List<SuggestParcelables$IntentParam> intentParams;
    public SuggestParcelables$IntentType intentType;
    public String mimeType;
    public String packageName;
    public String uri;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$IntentInfo(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$IntentInfo() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.intentParams == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.intentParams.size());
            for (SuggestParcelables$IntentParam suggestParcelables$IntentParam : this.intentParams) {
                if (suggestParcelables$IntentParam == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$IntentParam.writeToParcel(parcel, i);
                }
            }
        }
        if (this.packageName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.packageName);
        }
        if (this.className == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.className);
        }
        if (this.action == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.action);
        }
        if (this.uri == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.uri);
        }
        if (this.mimeType == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.mimeType);
        }
        parcel.writeInt(this.flags);
        if (this.intentType == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.intentType.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        SuggestParcelables$IntentParam suggestParcelables$IntentParam;
        if (parcel.readByte() == 0) {
            this.intentParams = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$IntentParam[] suggestParcelables$IntentParamArr = new SuggestParcelables$IntentParam[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$IntentParam = null;
                } else {
                    suggestParcelables$IntentParam = (SuggestParcelables$IntentParam) SuggestParcelables$IntentParam.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$IntentParamArr[i] = suggestParcelables$IntentParam;
            }
            this.intentParams = Arrays.asList(suggestParcelables$IntentParamArr);
        }
        if (parcel.readByte() == 0) {
            this.packageName = null;
        } else {
            this.packageName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.className = null;
        } else {
            this.className = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.action = null;
        } else {
            this.action = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.uri = null;
        } else {
            this.uri = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.mimeType = null;
        } else {
            this.mimeType = parcel.readString();
        }
        this.flags = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.intentType = null;
        } else {
            this.intentType = (SuggestParcelables$IntentType) SuggestParcelables$IntentType.CREATOR.createFromParcel(parcel);
        }
    }
}
