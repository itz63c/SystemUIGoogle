package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class SuggestParcelables$EntitySpan implements Parcelable {
    public static final Creator<SuggestParcelables$EntitySpan> CREATOR = new Creator<SuggestParcelables$EntitySpan>() {
        public SuggestParcelables$EntitySpan createFromParcel(Parcel parcel) {
            return new SuggestParcelables$EntitySpan(parcel);
        }

        public SuggestParcelables$EntitySpan[] newArray(int i) {
            return new SuggestParcelables$EntitySpan[i];
        }
    };
    public List<Integer> rectIndices;
    public List<SuggestParcelables$ContentRect> rects;
    public String selectionId;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$EntitySpan(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$EntitySpan() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.rects == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.rects.size());
            for (SuggestParcelables$ContentRect suggestParcelables$ContentRect : this.rects) {
                if (suggestParcelables$ContentRect == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$ContentRect.writeToParcel(parcel, i);
                }
            }
        }
        if (this.selectionId == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.selectionId);
        }
        if (this.rectIndices == null) {
            parcel.writeByte(0);
            return;
        }
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

    private void readFromParcel(Parcel parcel) {
        Integer num;
        SuggestParcelables$ContentRect suggestParcelables$ContentRect;
        if (parcel.readByte() == 0) {
            this.rects = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$ContentRect[] suggestParcelables$ContentRectArr = new SuggestParcelables$ContentRect[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$ContentRect = null;
                } else {
                    suggestParcelables$ContentRect = (SuggestParcelables$ContentRect) SuggestParcelables$ContentRect.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$ContentRectArr[i] = suggestParcelables$ContentRect;
            }
            this.rects = Arrays.asList(suggestParcelables$ContentRectArr);
        }
        if (parcel.readByte() == 0) {
            this.selectionId = null;
        } else {
            this.selectionId = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.rectIndices = null;
            return;
        }
        int readInt2 = parcel.readInt();
        Integer[] numArr = new Integer[readInt2];
        for (int i2 = 0; i2 < readInt2; i2++) {
            if (parcel.readByte() == 0) {
                num = null;
            } else {
                num = Integer.valueOf(parcel.readInt());
            }
            numArr[i2] = num;
        }
        this.rectIndices = Arrays.asList(numArr);
    }
}
