package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class SuggestParcelables$Entity implements Parcelable {
    public static final Creator<SuggestParcelables$Entity> CREATOR = new Creator<SuggestParcelables$Entity>() {
        public SuggestParcelables$Entity createFromParcel(Parcel parcel) {
            return new SuggestParcelables$Entity(parcel);
        }

        public SuggestParcelables$Entity[] newArray(int i) {
            return new SuggestParcelables$Entity[i];
        }
    };
    public List<SuggestParcelables$ActionGroup> actions;
    public float annotationScore;
    public String annotationSourceName;
    public String annotationTypeName;
    public int contentGroupIndex;
    public int endIndex;
    public List<SuggestParcelables$EntitySpan> entitySpans;

    /* renamed from: id */
    public String f99id;
    public SuggestParcelables$InteractionType interactionType;
    public boolean isSmartSelection;
    public int numWords;
    public String opaquePayload;
    public String searchQueryHint;
    public int selectionIndex;
    public int startIndex;
    public int suggestedPresentationMode;
    public String verticalTypeName;

    public int describeContents() {
        return 0;
    }

    public SuggestParcelables$Entity(Parcel parcel) {
        readFromParcel(parcel);
    }

    private SuggestParcelables$Entity() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f99id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f99id);
        }
        if (this.actions == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.actions.size());
            for (SuggestParcelables$ActionGroup suggestParcelables$ActionGroup : this.actions) {
                if (suggestParcelables$ActionGroup == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$ActionGroup.writeToParcel(parcel, i);
                }
            }
        }
        if (this.entitySpans == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.entitySpans.size());
            for (SuggestParcelables$EntitySpan suggestParcelables$EntitySpan : this.entitySpans) {
                if (suggestParcelables$EntitySpan == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$EntitySpan.writeToParcel(parcel, i);
                }
            }
        }
        if (this.searchQueryHint == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.searchQueryHint);
        }
        if (this.annotationTypeName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.annotationTypeName);
        }
        if (this.annotationSourceName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.annotationSourceName);
        }
        if (this.verticalTypeName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.verticalTypeName);
        }
        parcel.writeFloat(this.annotationScore);
        parcel.writeInt(this.contentGroupIndex);
        parcel.writeInt(this.selectionIndex);
        if (this.isSmartSelection) {
            parcel.writeByte(1);
        } else {
            parcel.writeByte(0);
        }
        parcel.writeInt(this.suggestedPresentationMode);
        parcel.writeInt(this.numWords);
        parcel.writeInt(this.startIndex);
        parcel.writeInt(this.endIndex);
        if (this.opaquePayload == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.opaquePayload);
        }
        if (this.interactionType == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.interactionType.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        SuggestParcelables$EntitySpan suggestParcelables$EntitySpan;
        SuggestParcelables$ActionGroup suggestParcelables$ActionGroup;
        if (parcel.readByte() == 0) {
            this.f99id = null;
        } else {
            this.f99id = parcel.readString();
        }
        boolean z = false;
        if (parcel.readByte() == 0) {
            this.actions = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$ActionGroup[] suggestParcelables$ActionGroupArr = new SuggestParcelables$ActionGroup[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$ActionGroup = null;
                } else {
                    suggestParcelables$ActionGroup = (SuggestParcelables$ActionGroup) SuggestParcelables$ActionGroup.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$ActionGroupArr[i] = suggestParcelables$ActionGroup;
            }
            this.actions = Arrays.asList(suggestParcelables$ActionGroupArr);
        }
        if (parcel.readByte() == 0) {
            this.entitySpans = null;
        } else {
            int readInt2 = parcel.readInt();
            SuggestParcelables$EntitySpan[] suggestParcelables$EntitySpanArr = new SuggestParcelables$EntitySpan[readInt2];
            for (int i2 = 0; i2 < readInt2; i2++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$EntitySpan = null;
                } else {
                    suggestParcelables$EntitySpan = (SuggestParcelables$EntitySpan) SuggestParcelables$EntitySpan.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$EntitySpanArr[i2] = suggestParcelables$EntitySpan;
            }
            this.entitySpans = Arrays.asList(suggestParcelables$EntitySpanArr);
        }
        if (parcel.readByte() == 0) {
            this.searchQueryHint = null;
        } else {
            this.searchQueryHint = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.annotationTypeName = null;
        } else {
            this.annotationTypeName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.annotationSourceName = null;
        } else {
            this.annotationSourceName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.verticalTypeName = null;
        } else {
            this.verticalTypeName = parcel.readString();
        }
        this.annotationScore = parcel.readFloat();
        this.contentGroupIndex = parcel.readInt();
        this.selectionIndex = parcel.readInt();
        if (parcel.readByte() == 1) {
            z = true;
        }
        this.isSmartSelection = z;
        this.suggestedPresentationMode = parcel.readInt();
        this.numWords = parcel.readInt();
        this.startIndex = parcel.readInt();
        this.endIndex = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.opaquePayload = null;
        } else {
            this.opaquePayload = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.interactionType = null;
        } else {
            this.interactionType = (SuggestParcelables$InteractionType) SuggestParcelables$InteractionType.CREATOR.createFromParcel(parcel);
        }
    }
}
