package com.android.settingslib.drawer;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

public class DashboardCategory implements Parcelable {
    public static final Creator<DashboardCategory> CREATOR = new Creator<DashboardCategory>() {
        public DashboardCategory createFromParcel(Parcel parcel) {
            return new DashboardCategory(parcel);
        }

        public DashboardCategory[] newArray(int i) {
            return new DashboardCategory[i];
        }
    };
    public final String key;
    private List<Tile> mTiles = new ArrayList();

    public int describeContents() {
        return 0;
    }

    DashboardCategory(Parcel parcel) {
        this.key = parcel.readString();
        int readInt = parcel.readInt();
        for (int i = 0; i < readInt; i++) {
            this.mTiles.add((Tile) Tile.CREATOR.createFromParcel(parcel));
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.key);
        int size = this.mTiles.size();
        parcel.writeInt(size);
        for (int i2 = 0; i2 < size; i2++) {
            ((Tile) this.mTiles.get(i2)).writeToParcel(parcel, i);
        }
    }
}
