package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$SelectionDetail implements Parcelable {
    public static final Creator<FeedbackParcelables$SelectionDetail> CREATOR = new Creator<FeedbackParcelables$SelectionDetail>() {
        public FeedbackParcelables$SelectionDetail createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$SelectionDetail(parcel);
        }

        public FeedbackParcelables$SelectionDetail[] newArray(int i) {
            return new FeedbackParcelables$SelectionDetail[i];
        }
    };
    public boolean selectionModified;
    public int smartSelectionRangeEnd;
    public int smartSelectionRangeStart;
    public boolean smartSelectionSingleWord;
    public int userSelectionRangeEnd;
    public int userSelectionRangeStart;
    public boolean userSelectionSingleWord;

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$SelectionDetail(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$SelectionDetail() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.smartSelectionSingleWord) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.userSelectionSingleWord) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.selectionModified) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        parcel.writeInt(this.smartSelectionRangeStart);
        parcel.writeInt(this.smartSelectionRangeEnd);
        parcel.writeInt(this.userSelectionRangeStart);
        parcel.writeInt(this.userSelectionRangeEnd);
    }

    private void readFromParcel(Parcel parcel) {
        boolean z = false;
        this.smartSelectionSingleWord = parcel.readByte() == 1;
        this.userSelectionSingleWord = parcel.readByte() == 1;
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.selectionModified = z;
        this.smartSelectionRangeStart = parcel.readInt();
        this.smartSelectionRangeEnd = parcel.readInt();
        this.userSelectionRangeStart = parcel.readInt();
        this.userSelectionRangeEnd = parcel.readInt();
    }
}
