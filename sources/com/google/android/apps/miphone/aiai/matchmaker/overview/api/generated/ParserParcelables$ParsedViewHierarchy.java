package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class ParserParcelables$ParsedViewHierarchy implements Parcelable {
    public static final Creator<ParserParcelables$ParsedViewHierarchy> CREATOR = new Creator<ParserParcelables$ParsedViewHierarchy>() {
        public ParserParcelables$ParsedViewHierarchy createFromParcel(Parcel parcel) {
            return new ParserParcelables$ParsedViewHierarchy(parcel);
        }

        public ParserParcelables$ParsedViewHierarchy[] newArray(int i) {
            return new ParserParcelables$ParsedViewHierarchy[i];
        }
    };
    public long acquisitionEndTime;
    public long acquisitionStartTime;
    public String activityClassName;
    public boolean hasKnownIssues;
    public boolean isHomeActivity;
    public String packageName;
    public List<ParserParcelables$WindowNode> windows;

    public int describeContents() {
        return 0;
    }

    public ParserParcelables$ParsedViewHierarchy(Parcel parcel) {
        readFromParcel(parcel);
    }

    private ParserParcelables$ParsedViewHierarchy() {
    }

    public static ParserParcelables$ParsedViewHierarchy create() {
        return new ParserParcelables$ParsedViewHierarchy();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.acquisitionStartTime);
        parcel.writeLong(this.acquisitionEndTime);
        if (this.isHomeActivity) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.windows == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.windows.size());
            for (ParserParcelables$WindowNode parserParcelables$WindowNode : this.windows) {
                if (parserParcelables$WindowNode == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    parserParcelables$WindowNode.writeToParcel(parcel, i);
                }
            }
        }
        if (this.hasKnownIssues) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.packageName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.packageName);
        }
        if (this.activityClassName == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.activityClassName);
    }

    private void readFromParcel(Parcel parcel) {
        ParserParcelables$WindowNode parserParcelables$WindowNode;
        this.acquisitionStartTime = parcel.readLong();
        this.acquisitionEndTime = parcel.readLong();
        boolean z = false;
        this.isHomeActivity = parcel.readByte() == 1;
        if (parcel.readByte() == 0) {
            this.windows = null;
        } else {
            int readInt = parcel.readInt();
            ParserParcelables$WindowNode[] parserParcelables$WindowNodeArr = new ParserParcelables$WindowNode[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    parserParcelables$WindowNode = null;
                } else {
                    parserParcelables$WindowNode = (ParserParcelables$WindowNode) ParserParcelables$WindowNode.CREATOR.createFromParcel(parcel);
                }
                parserParcelables$WindowNodeArr[i] = parserParcelables$WindowNode;
            }
            this.windows = Arrays.asList(parserParcelables$WindowNodeArr);
        }
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.hasKnownIssues = z;
        if (parcel.readByte() == 0) {
            this.packageName = null;
        } else {
            this.packageName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.activityClassName = null;
        } else {
            this.activityClassName = parcel.readString();
        }
    }
}
