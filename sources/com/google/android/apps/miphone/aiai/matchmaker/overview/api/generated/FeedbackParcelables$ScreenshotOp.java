package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum FeedbackParcelables$ScreenshotOp implements Parcelable {
    OP_UNKNOWN(0),
    RETRIEVE_SMART_ACTIONS(1),
    REQUEST_SMART_ACTIONS(2),
    WAIT_FOR_SMART_ACTIONS(3);
    
    public static final Creator<FeedbackParcelables$ScreenshotOp> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<FeedbackParcelables$ScreenshotOp>() {
            public FeedbackParcelables$ScreenshotOp createFromParcel(Parcel parcel) {
                return FeedbackParcelables$ScreenshotOp.create(parcel);
            }

            public FeedbackParcelables$ScreenshotOp[] newArray(int i) {
                return new FeedbackParcelables$ScreenshotOp[i];
            }
        };
    }

    private FeedbackParcelables$ScreenshotOp(int i) {
        this.value = i;
    }

    public static FeedbackParcelables$ScreenshotOp create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static FeedbackParcelables$ScreenshotOp create(int i) {
        if (i == 0) {
            return OP_UNKNOWN;
        }
        if (i == 1) {
            return RETRIEVE_SMART_ACTIONS;
        }
        if (i == 2) {
            return REQUEST_SMART_ACTIONS;
        }
        if (i == 3) {
            return WAIT_FOR_SMART_ACTIONS;
        }
        StringBuilder sb = new StringBuilder(26);
        sb.append("Invalid value: ");
        sb.append(i);
        throw new RuntimeException(sb.toString());
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.value);
    }
}
