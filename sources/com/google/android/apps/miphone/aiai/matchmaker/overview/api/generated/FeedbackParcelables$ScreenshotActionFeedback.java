package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$ScreenshotActionFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$ScreenshotActionFeedback> CREATOR = new Creator<FeedbackParcelables$ScreenshotActionFeedback>() {
        public FeedbackParcelables$ScreenshotActionFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$ScreenshotActionFeedback(parcel);
        }

        public FeedbackParcelables$ScreenshotActionFeedback[] newArray(int i) {
            return new FeedbackParcelables$ScreenshotActionFeedback[i];
        }
    };
    public String actionType;
    public boolean isSmartActions;

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$ScreenshotActionFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$ScreenshotActionFeedback() {
    }

    public static FeedbackParcelables$ScreenshotActionFeedback create() {
        return new FeedbackParcelables$ScreenshotActionFeedback();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.actionType == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.actionType);
        }
        if (this.isSmartActions) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.actionType = null;
        } else {
            this.actionType = parcel.readString();
        }
        boolean z = true;
        if (parcel.readByte() != 1) {
            z = false;
        }
        this.isSmartActions = z;
    }
}
