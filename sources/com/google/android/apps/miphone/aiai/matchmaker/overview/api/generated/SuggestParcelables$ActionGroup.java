package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class SuggestParcelables$ActionGroup implements Parcelable {
    public static final Creator<SuggestParcelables$ActionGroup> CREATOR = new Creator<SuggestParcelables$ActionGroup>() {
        public SuggestParcelables$ActionGroup createFromParcel(Parcel parcel) {
            return new SuggestParcelables$ActionGroup(parcel);
        }

        public SuggestParcelables$ActionGroup[] newArray(int i) {
            return new SuggestParcelables$ActionGroup[i];
        }
    };
    public List<SuggestParcelables$Action> alternateActions;
    public String displayName;

    /* renamed from: id */
    public String f97id;
    public boolean isHiddenAction;
    public SuggestParcelables$Action mainAction;
    public String opaquePayload;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$ActionGroup(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$ActionGroup() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f97id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f97id);
        }
        if (this.displayName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.displayName);
        }
        if (this.mainAction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.mainAction.writeToParcel(parcel, i);
        }
        if (this.alternateActions == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.alternateActions.size());
            for (SuggestParcelables$Action suggestParcelables$Action : this.alternateActions) {
                if (suggestParcelables$Action == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$Action.writeToParcel(parcel, i);
                }
            }
        }
        if (this.isHiddenAction) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.opaquePayload == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.opaquePayload);
    }

    private void readFromParcel(Parcel parcel) {
        SuggestParcelables$Action suggestParcelables$Action;
        if (parcel.readByte() == 0) {
            this.f97id = null;
        } else {
            this.f97id = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.displayName = null;
        } else {
            this.displayName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.mainAction = null;
        } else {
            this.mainAction = (SuggestParcelables$Action) SuggestParcelables$Action.CREATOR.createFromParcel(parcel);
        }
        boolean z = false;
        if (parcel.readByte() == 0) {
            this.alternateActions = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$Action[] suggestParcelables$ActionArr = new SuggestParcelables$Action[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$Action = null;
                } else {
                    suggestParcelables$Action = (SuggestParcelables$Action) SuggestParcelables$Action.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$ActionArr[i] = suggestParcelables$Action;
            }
            this.alternateActions = Arrays.asList(suggestParcelables$ActionArr);
        }
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.isHiddenAction = z;
        if (parcel.readByte() == 0) {
            this.opaquePayload = null;
        } else {
            this.opaquePayload = parcel.readString();
        }
    }
}
