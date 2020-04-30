package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class SuggestParcelables$Entities implements Parcelable {
    public static final Creator<SuggestParcelables$Entities> CREATOR = new Creator<SuggestParcelables$Entities>() {
        public SuggestParcelables$Entities createFromParcel(Parcel parcel) {
            return new SuggestParcelables$Entities(parcel);
        }

        public SuggestParcelables$Entities[] newArray(int i) {
            return new SuggestParcelables$Entities[i];
        }
    };
    public SuggestParcelables$DebugInfo debugInfo;
    public List<SuggestParcelables$Entity> entities;
    public SuggestParcelables$ExtrasInfo extrasInfo;

    /* renamed from: id */
    public String f98id;
    public String opaquePayload;
    public SuggestParcelables$Stats stats;
    public boolean success;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$Entities(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$Entities() {
    }

    public static SuggestParcelables$Entities create() {
        return new SuggestParcelables$Entities();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f98id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f98id);
        }
        if (this.success) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.entities == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.entities.size());
            for (SuggestParcelables$Entity suggestParcelables$Entity : this.entities) {
                if (suggestParcelables$Entity == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$Entity.writeToParcel(parcel, i);
                }
            }
        }
        if (this.stats == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.stats.writeToParcel(parcel, i);
        }
        if (this.debugInfo == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.debugInfo.writeToParcel(parcel, i);
        }
        if (this.extrasInfo == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.extrasInfo.writeToParcel(parcel, i);
        }
        if (this.opaquePayload == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.opaquePayload);
    }

    private void readFromParcel(Parcel parcel) {
        SuggestParcelables$Entity suggestParcelables$Entity;
        if (parcel.readByte() == 0) {
            this.f98id = null;
        } else {
            this.f98id = parcel.readString();
        }
        boolean z = true;
        if (parcel.readByte() != 1) {
            z = false;
        }
        this.success = z;
        if (parcel.readByte() == 0) {
            this.entities = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$Entity[] suggestParcelables$EntityArr = new SuggestParcelables$Entity[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$Entity = null;
                } else {
                    suggestParcelables$Entity = (SuggestParcelables$Entity) SuggestParcelables$Entity.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$EntityArr[i] = suggestParcelables$Entity;
            }
            this.entities = Arrays.asList(suggestParcelables$EntityArr);
        }
        if (parcel.readByte() == 0) {
            this.stats = null;
        } else {
            this.stats = (SuggestParcelables$Stats) SuggestParcelables$Stats.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.debugInfo = null;
        } else {
            this.debugInfo = (SuggestParcelables$DebugInfo) SuggestParcelables$DebugInfo.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.extrasInfo = null;
        } else {
            this.extrasInfo = (SuggestParcelables$ExtrasInfo) SuggestParcelables$ExtrasInfo.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.opaquePayload = null;
        } else {
            this.opaquePayload = parcel.readString();
        }
    }
}
