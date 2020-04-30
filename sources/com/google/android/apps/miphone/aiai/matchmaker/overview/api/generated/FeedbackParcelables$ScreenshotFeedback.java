package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$ScreenshotFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$ScreenshotFeedback> CREATOR = new Creator<FeedbackParcelables$ScreenshotFeedback>() {
        public FeedbackParcelables$ScreenshotFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$ScreenshotFeedback(parcel);
        }

        public FeedbackParcelables$ScreenshotFeedback[] newArray(int i) {
            return new FeedbackParcelables$ScreenshotFeedback[i];
        }
    };
    public Object screenshotFeedback;
    public String screenshotId;

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$ScreenshotFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$ScreenshotFeedback() {
    }

    public static FeedbackParcelables$ScreenshotFeedback create() {
        return new FeedbackParcelables$ScreenshotFeedback();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.screenshotFeedback == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            if (this.screenshotFeedback instanceof FeedbackParcelables$ScreenshotOpFeedback) {
                parcel.writeInt(2);
                ((FeedbackParcelables$ScreenshotOpFeedback) this.screenshotFeedback).writeToParcel(parcel, i);
            }
            if (this.screenshotFeedback instanceof FeedbackParcelables$ScreenshotActionFeedback) {
                parcel.writeInt(3);
                ((FeedbackParcelables$ScreenshotActionFeedback) this.screenshotFeedback).writeToParcel(parcel, i);
            }
        }
        if (this.screenshotId == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.screenshotId);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.screenshotFeedback = null;
        } else {
            int readInt = parcel.readInt();
            if (readInt == 2) {
                this.screenshotFeedback = FeedbackParcelables$ScreenshotOpFeedback.CREATOR.createFromParcel(parcel);
            }
            if (readInt == 3) {
                this.screenshotFeedback = FeedbackParcelables$ScreenshotActionFeedback.CREATOR.createFromParcel(parcel);
            }
        }
        if (parcel.readByte() == 0) {
            this.screenshotId = null;
        } else {
            this.screenshotId = parcel.readString();
        }
    }
}
