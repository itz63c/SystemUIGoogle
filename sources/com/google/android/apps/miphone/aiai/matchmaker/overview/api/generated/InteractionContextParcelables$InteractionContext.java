package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class InteractionContextParcelables$InteractionContext implements Parcelable {
    public static final Creator<InteractionContextParcelables$InteractionContext> CREATOR = new Creator<InteractionContextParcelables$InteractionContext>() {
        public InteractionContextParcelables$InteractionContext createFromParcel(Parcel parcel) {
            return new InteractionContextParcelables$InteractionContext(parcel);
        }

        public InteractionContextParcelables$InteractionContext[] newArray(int i) {
            return new InteractionContextParcelables$InteractionContext[i];
        }
    };
    public boolean disallowCopyPaste;
    public boolean expandFocusRect;
    public SuggestParcelables$OnScreenRect focusRect;
    public int focusRectExpandPx;
    public List<InteractionContextParcelables$InteractionEvent> interactionEvents;
    public SuggestParcelables$InteractionType interactionType;
    public boolean isPrimaryTask;
    public boolean isRtlContent;
    public ContentParcelables$Contents previousContents;
    public boolean requestDebugInfo;
    public boolean requestStats;
    public long screenSessionId;
    public int versionCode;

    public int describeContents() {
        return 0;
    }

    public InteractionContextParcelables$InteractionContext(Parcel parcel) {
        readFromParcel(parcel);
    }

    private InteractionContextParcelables$InteractionContext() {
    }

    public static InteractionContextParcelables$InteractionContext create() {
        return new InteractionContextParcelables$InteractionContext();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.screenSessionId);
        if (this.focusRect == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.focusRect.writeToParcel(parcel, i);
        }
        if (this.expandFocusRect) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        parcel.writeInt(this.focusRectExpandPx);
        if (this.previousContents == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.previousContents.writeToParcel(parcel, i);
        }
        if (this.requestStats) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.requestDebugInfo) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.isRtlContent) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        if (this.disallowCopyPaste) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        parcel.writeInt(this.versionCode);
        if (this.interactionEvents == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.interactionEvents.size());
            for (InteractionContextParcelables$InteractionEvent interactionContextParcelables$InteractionEvent : this.interactionEvents) {
                if (interactionContextParcelables$InteractionEvent == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    interactionContextParcelables$InteractionEvent.writeToParcel(parcel, i);
                }
            }
        }
        if (this.interactionType == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.interactionType.writeToParcel(parcel, i);
        }
        if (this.isPrimaryTask) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
    }

    private void readFromParcel(Parcel parcel) {
        InteractionContextParcelables$InteractionEvent interactionContextParcelables$InteractionEvent;
        this.screenSessionId = parcel.readLong();
        if (parcel.readByte() == 0) {
            this.focusRect = null;
        } else {
            this.focusRect = (SuggestParcelables$OnScreenRect) SuggestParcelables$OnScreenRect.CREATOR.createFromParcel(parcel);
        }
        boolean z = false;
        this.expandFocusRect = parcel.readByte() == 1;
        this.focusRectExpandPx = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.previousContents = null;
        } else {
            this.previousContents = (ContentParcelables$Contents) ContentParcelables$Contents.CREATOR.createFromParcel(parcel);
        }
        this.requestStats = parcel.readByte() == 1;
        this.requestDebugInfo = parcel.readByte() == 1;
        this.isRtlContent = parcel.readByte() == 1;
        this.disallowCopyPaste = parcel.readByte() == 1;
        this.versionCode = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.interactionEvents = null;
        } else {
            int readInt = parcel.readInt();
            InteractionContextParcelables$InteractionEvent[] interactionContextParcelables$InteractionEventArr = new InteractionContextParcelables$InteractionEvent[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    interactionContextParcelables$InteractionEvent = null;
                } else {
                    interactionContextParcelables$InteractionEvent = (InteractionContextParcelables$InteractionEvent) InteractionContextParcelables$InteractionEvent.CREATOR.createFromParcel(parcel);
                }
                interactionContextParcelables$InteractionEventArr[i] = interactionContextParcelables$InteractionEvent;
            }
            this.interactionEvents = Arrays.asList(interactionContextParcelables$InteractionEventArr);
        }
        if (parcel.readByte() == 0) {
            this.interactionType = null;
        } else {
            this.interactionType = (SuggestParcelables$InteractionType) SuggestParcelables$InteractionType.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.isPrimaryTask = z;
    }
}
