package com.android.settingslib.schedulesprovider;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ScheduleInfo implements Parcelable {
    public static final Creator<ScheduleInfo> CREATOR = new Creator<ScheduleInfo>() {
        public ScheduleInfo createFromParcel(Parcel parcel) {
            return new ScheduleInfo(parcel);
        }

        public ScheduleInfo[] newArray(int i) {
            return new ScheduleInfo[i];
        }
    };
    private final PendingIntent mPendingIntent;
    private final String mSummary;
    private final String mTitle;

    public int describeContents() {
        return 0;
    }

    private ScheduleInfo(Parcel parcel) {
        this.mTitle = parcel.readString();
        this.mSummary = parcel.readString();
        this.mPendingIntent = (PendingIntent) parcel.readParcelable(PendingIntent.class.getClassLoader());
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mTitle);
        parcel.writeString(this.mSummary);
        parcel.writeParcelable(this.mPendingIntent, i);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("title: ");
        sb.append(this.mTitle);
        sb.append(", summary: ");
        sb.append(this.mSummary);
        sb.append(", pendingIntent: ");
        sb.append(this.mPendingIntent);
        return sb.toString();
    }
}
