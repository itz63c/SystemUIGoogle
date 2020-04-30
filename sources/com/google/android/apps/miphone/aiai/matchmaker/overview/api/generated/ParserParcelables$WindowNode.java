package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class ParserParcelables$WindowNode implements Parcelable {
    public static final Creator<ParserParcelables$WindowNode> CREATOR = new Creator<ParserParcelables$WindowNode>() {
        public ParserParcelables$WindowNode createFromParcel(Parcel parcel) {
            return new ParserParcelables$WindowNode(parcel);
        }

        public ParserParcelables$WindowNode[] newArray(int i) {
            return new ParserParcelables$WindowNode[i];
        }
    };
    public int displayId;
    public int height;
    public int left;
    public ParserParcelables$ViewNode rootViewNode;
    public int top;
    public int width;

    public int describeContents() {
        return 0;
    }

    public ParserParcelables$WindowNode(Parcel parcel) {
        readFromParcel(parcel);
    }

    private ParserParcelables$WindowNode() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.displayId);
        if (this.rootViewNode == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.rootViewNode.writeToParcel(parcel, i);
        }
        parcel.writeInt(this.left);
        parcel.writeInt(this.top);
        parcel.writeInt(this.width);
        parcel.writeInt(this.height);
    }

    private void readFromParcel(Parcel parcel) {
        this.displayId = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.rootViewNode = null;
        } else {
            this.rootViewNode = (ParserParcelables$ViewNode) ParserParcelables$ViewNode.CREATOR.createFromParcel(parcel);
        }
        this.left = parcel.readInt();
        this.top = parcel.readInt();
        this.width = parcel.readInt();
        this.height = parcel.readInt();
    }
}
