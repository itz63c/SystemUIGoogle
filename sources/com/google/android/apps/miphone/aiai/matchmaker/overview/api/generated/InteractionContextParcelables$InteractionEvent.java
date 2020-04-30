package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class InteractionContextParcelables$InteractionEvent implements Parcelable {
    public static final Creator<InteractionContextParcelables$InteractionEvent> CREATOR = new Creator<InteractionContextParcelables$InteractionEvent>() {
        public InteractionContextParcelables$InteractionEvent createFromParcel(Parcel parcel) {
            return new InteractionContextParcelables$InteractionEvent(parcel);
        }

        public InteractionContextParcelables$InteractionEvent[] newArray(int i) {
            return new InteractionContextParcelables$InteractionEvent[i];
        }
    };
    public int action;
    public int actionButton;
    public int actionIndex;
    public int actionMasked;
    public SuggestParcelables$OnScreenRect bitmapCropRect;
    public float bitmapScaleX;
    public float bitmapScaleY;
    public int buttonState;
    public int deviceId;
    public long downTimeMs;
    public int edgeFlags;
    public long eventTimeMs;
    public int motionEventFlags;
    public float orientation;
    public float rawX;
    public float rawY;
    public int source;
    public float toolMajor;
    public float toolMinor;

    /* renamed from: x */
    public float f93x;
    public float xPrecision;

    /* renamed from: y */
    public float f94y;
    public float yPrecision;

    public int describeContents() {
        return 0;
    }

    public InteractionContextParcelables$InteractionEvent(Parcel parcel) {
        readFromParcel(parcel);
    }

    private InteractionContextParcelables$InteractionEvent() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.action);
        parcel.writeInt(this.actionButton);
        parcel.writeInt(this.actionIndex);
        parcel.writeInt(this.actionMasked);
        parcel.writeInt(this.buttonState);
        parcel.writeInt(this.deviceId);
        parcel.writeLong(this.downTimeMs);
        parcel.writeInt(this.edgeFlags);
        parcel.writeInt(this.motionEventFlags);
        parcel.writeFloat(this.orientation);
        parcel.writeFloat(this.rawX);
        parcel.writeFloat(this.rawY);
        parcel.writeInt(this.source);
        parcel.writeFloat(this.toolMajor);
        parcel.writeFloat(this.toolMinor);
        parcel.writeFloat(this.f93x);
        parcel.writeFloat(this.f94y);
        parcel.writeFloat(this.xPrecision);
        parcel.writeFloat(this.yPrecision);
        if (this.bitmapCropRect == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.bitmapCropRect.writeToParcel(parcel, i);
        }
        parcel.writeFloat(this.bitmapScaleX);
        parcel.writeFloat(this.bitmapScaleY);
        parcel.writeLong(this.eventTimeMs);
    }

    private void readFromParcel(Parcel parcel) {
        this.action = parcel.readInt();
        this.actionButton = parcel.readInt();
        this.actionIndex = parcel.readInt();
        this.actionMasked = parcel.readInt();
        this.buttonState = parcel.readInt();
        this.deviceId = parcel.readInt();
        this.downTimeMs = parcel.readLong();
        this.edgeFlags = parcel.readInt();
        this.motionEventFlags = parcel.readInt();
        this.orientation = parcel.readFloat();
        this.rawX = parcel.readFloat();
        this.rawY = parcel.readFloat();
        this.source = parcel.readInt();
        this.toolMajor = parcel.readFloat();
        this.toolMinor = parcel.readFloat();
        this.f93x = parcel.readFloat();
        this.f94y = parcel.readFloat();
        this.xPrecision = parcel.readFloat();
        this.yPrecision = parcel.readFloat();
        if (parcel.readByte() == 0) {
            this.bitmapCropRect = null;
        } else {
            this.bitmapCropRect = (SuggestParcelables$OnScreenRect) SuggestParcelables$OnScreenRect.CREATOR.createFromParcel(parcel);
        }
        this.bitmapScaleX = parcel.readFloat();
        this.bitmapScaleY = parcel.readFloat();
        this.eventTimeMs = parcel.readLong();
    }
}
