package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class ContentParcelables$Contents implements Parcelable {
    public static final Creator<ContentParcelables$Contents> CREATOR = new Creator<ContentParcelables$Contents>() {
        public ContentParcelables$Contents createFromParcel(Parcel parcel) {
            return new ContentParcelables$Contents(parcel);
        }

        public ContentParcelables$Contents[] newArray(int i) {
            return new ContentParcelables$Contents[i];
        }
    };
    public List<ContentParcelables$ContentGroup> contentGroups;
    public SuggestParcelables$DebugInfo debugInfo;

    /* renamed from: id */
    public String f88id;
    public String opaquePayload;
    public long screenSessionId;
    public SuggestParcelables$Stats stats;

    public int describeContents() {
        return 0;
    }

    public ContentParcelables$Contents(Parcel parcel) {
        readFromParcel(parcel);
    }

    private ContentParcelables$Contents() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f88id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f88id);
        }
        parcel.writeLong(this.screenSessionId);
        if (this.contentGroups == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.contentGroups.size());
            for (ContentParcelables$ContentGroup contentParcelables$ContentGroup : this.contentGroups) {
                if (contentParcelables$ContentGroup == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    contentParcelables$ContentGroup.writeToParcel(parcel, i);
                }
            }
        }
        if (this.stats == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.stats.writeToParcel(parcel, i);
        }
        if (this.debugInfo == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.debugInfo.writeToParcel(parcel, i);
        }
        if (this.opaquePayload == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.opaquePayload);
    }

    private void readFromParcel(Parcel parcel) {
        ContentParcelables$ContentGroup contentParcelables$ContentGroup;
        if (parcel.readByte() == 0) {
            this.f88id = null;
        } else {
            this.f88id = parcel.readString();
        }
        this.screenSessionId = parcel.readLong();
        if (parcel.readByte() == 0) {
            this.contentGroups = null;
        } else {
            int readInt = parcel.readInt();
            ContentParcelables$ContentGroup[] contentParcelables$ContentGroupArr = new ContentParcelables$ContentGroup[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    contentParcelables$ContentGroup = null;
                } else {
                    contentParcelables$ContentGroup = (ContentParcelables$ContentGroup) ContentParcelables$ContentGroup.CREATOR.createFromParcel(parcel);
                }
                contentParcelables$ContentGroupArr[i] = contentParcelables$ContentGroup;
            }
            this.contentGroups = Arrays.asList(contentParcelables$ContentGroupArr);
        }
        if (parcel.readByte() == 0) {
            this.stats = null;
        } else {
            this.stats = (SuggestParcelables$Stats) SuggestParcelables$Stats.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.debugInfo = null;
        } else {
            this.debugInfo = (SuggestParcelables$DebugInfo) SuggestParcelables$DebugInfo.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.opaquePayload = null;
        } else {
            this.opaquePayload = parcel.readString();
        }
    }
}
