package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum FeedbackParcelables$ScreenshotOpStatus implements Parcelable {
    OP_STATUS_UNKNOWN(0),
    SUCCESS(1),
    ERROR(2),
    TIMEOUT(3);
    
    public static final Creator<FeedbackParcelables$ScreenshotOpStatus> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<FeedbackParcelables$ScreenshotOpStatus>() {
            public FeedbackParcelables$ScreenshotOpStatus createFromParcel(Parcel parcel) {
                return FeedbackParcelables$ScreenshotOpStatus.create(parcel);
            }

            public FeedbackParcelables$ScreenshotOpStatus[] newArray(int i) {
                return new FeedbackParcelables$ScreenshotOpStatus[i];
            }
        };
    }

    private FeedbackParcelables$ScreenshotOpStatus(int i) {
        this.value = i;
    }

    public static FeedbackParcelables$ScreenshotOpStatus create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static FeedbackParcelables$ScreenshotOpStatus create(int i) {
        if (i == 0) {
            return OP_STATUS_UNKNOWN;
        }
        if (i == 1) {
            return SUCCESS;
        }
        if (i == 2) {
            return ERROR;
        }
        if (i == 3) {
            return TIMEOUT;
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
