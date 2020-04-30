package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class ContentParcelables$Selection implements Parcelable {
    public static final Creator<ContentParcelables$Selection> CREATOR = new Creator<ContentParcelables$Selection>() {
        public ContentParcelables$Selection createFromParcel(Parcel parcel) {
            return new ContentParcelables$Selection(parcel);
        }

        public ContentParcelables$Selection[] newArray(int i) {
            return new ContentParcelables$Selection[i];
        }
    };

    /* renamed from: id */
    public String f89id;
    public SuggestParcelables$InteractionType interactionType;
    public boolean isSmartSelection;
    public String opaquePayload;
    public List<Integer> rectIndices;
    public int suggestedPresentationMode;

    public int describeContents() {
        return 0;
    }

    public ContentParcelables$Selection(Parcel parcel) {
        readFromParcel(parcel);
    }

    private ContentParcelables$Selection() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.rectIndices == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.rectIndices.size());
            for (Integer num : this.rectIndices) {
                if (num == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parcel.writeInt(num.intValue());
                }
            }
        }
        if (this.f89id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f89id);
        }
        if (this.isSmartSelection) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        parcel.writeInt(this.suggestedPresentationMode);
        if (this.opaquePayload == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.opaquePayload);
        }
        if (this.interactionType == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.interactionType.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        Integer num;
        boolean z = false;
        if (parcel.readByte() == 0) {
            this.rectIndices = null;
        } else {
            int readInt = parcel.readInt();
            Integer[] numArr = new Integer[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    num = null;
                } else {
                    num = Integer.valueOf(parcel.readInt());
                }
                numArr[i] = num;
            }
            this.rectIndices = Arrays.asList(numArr);
        }
        if (parcel.readByte() == 0) {
            this.f89id = null;
        } else {
            this.f89id = parcel.readString();
        }
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.isSmartSelection = z;
        this.suggestedPresentationMode = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.opaquePayload = null;
        } else {
            this.opaquePayload = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.interactionType = null;
        } else {
            this.interactionType = (SuggestParcelables$InteractionType) SuggestParcelables$InteractionType.CREATOR.createFromParcel(parcel);
        }
    }
}
