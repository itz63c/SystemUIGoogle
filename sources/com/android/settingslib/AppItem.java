package com.android.settingslib;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.SparseBooleanArray;

public class AppItem implements Comparable<AppItem>, Parcelable {
    public static final Creator<AppItem> CREATOR = new Creator<AppItem>() {
        public AppItem createFromParcel(Parcel parcel) {
            return new AppItem(parcel);
        }

        public AppItem[] newArray(int i) {
            return new AppItem[i];
        }
    };
    public int category;
    public final int key;
    public long total;
    public SparseBooleanArray uids;

    public int describeContents() {
        return 0;
    }

    public AppItem() {
        this.uids = new SparseBooleanArray();
        this.key = 0;
    }

    public AppItem(Parcel parcel) {
        this.uids = new SparseBooleanArray();
        this.key = parcel.readInt();
        this.uids = parcel.readSparseBooleanArray();
        this.total = parcel.readLong();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.key);
        parcel.writeSparseBooleanArray(this.uids);
        parcel.writeLong(this.total);
    }

    public int compareTo(AppItem appItem) {
        int compare = Integer.compare(this.category, appItem.category);
        return compare == 0 ? Long.compare(appItem.total, this.total) : compare;
    }
}
