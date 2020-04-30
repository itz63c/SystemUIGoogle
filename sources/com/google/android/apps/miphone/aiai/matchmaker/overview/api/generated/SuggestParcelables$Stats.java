package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class SuggestParcelables$Stats implements Parcelable {
    public static final Creator<SuggestParcelables$Stats> CREATOR = new Creator<SuggestParcelables$Stats>() {
        public SuggestParcelables$Stats createFromParcel(Parcel parcel) {
            return new SuggestParcelables$Stats(parcel);
        }

        public SuggestParcelables$Stats[] newArray(int i) {
            return new SuggestParcelables$Stats[i];
        }
    };
    public long endTimestampMs;
    public long entityExtractionMs;
    public long ocrDetectionMs;
    public long ocrMs;
    public long startTimestampMs;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$Stats(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$Stats() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.startTimestampMs);
        parcel.writeLong(this.endTimestampMs);
        parcel.writeLong(this.ocrMs);
        parcel.writeLong(this.ocrDetectionMs);
        parcel.writeLong(this.entityExtractionMs);
    }

    private void readFromParcel(Parcel parcel) {
        this.startTimestampMs = parcel.readLong();
        this.endTimestampMs = parcel.readLong();
        this.ocrMs = parcel.readLong();
        this.ocrDetectionMs = parcel.readLong();
        this.entityExtractionMs = parcel.readLong();
    }
}
