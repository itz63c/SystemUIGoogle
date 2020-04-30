package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$ScreenshotOpFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$ScreenshotOpFeedback> CREATOR = new Creator<FeedbackParcelables$ScreenshotOpFeedback>() {
        public FeedbackParcelables$ScreenshotOpFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$ScreenshotOpFeedback(parcel);
        }

        public FeedbackParcelables$ScreenshotOpFeedback[] newArray(int i) {
            return new FeedbackParcelables$ScreenshotOpFeedback[i];
        }
    };
    public long durationMs;

    /* renamed from: op */
    public FeedbackParcelables$ScreenshotOp f92op;
    public FeedbackParcelables$ScreenshotOpStatus status;

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$ScreenshotOpFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$ScreenshotOpFeedback() {
    }

    public static FeedbackParcelables$ScreenshotOpFeedback create() {
        return new FeedbackParcelables$ScreenshotOpFeedback();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f92op == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.f92op.writeToParcel(parcel, i);
        }
        if (this.status == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.status.writeToParcel(parcel, i);
        }
        parcel.writeLong(this.durationMs);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.f92op = null;
        } else {
            this.f92op = (FeedbackParcelables$ScreenshotOp) FeedbackParcelables$ScreenshotOp.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.status = null;
        } else {
            this.status = (FeedbackParcelables$ScreenshotOpStatus) FeedbackParcelables$ScreenshotOpStatus.CREATOR.createFromParcel(parcel);
        }
        this.durationMs = parcel.readLong();
    }
}
