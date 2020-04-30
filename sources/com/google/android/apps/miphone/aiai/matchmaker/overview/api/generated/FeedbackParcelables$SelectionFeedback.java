package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$SelectionFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$SelectionFeedback> CREATOR = new Creator<FeedbackParcelables$SelectionFeedback>() {
        public FeedbackParcelables$SelectionFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$SelectionFeedback(parcel);
        }

        public FeedbackParcelables$SelectionFeedback[] newArray(int i) {
            return new FeedbackParcelables$SelectionFeedback[i];
        }
    };
    public String interactionSessionId;
    public SuggestParcelables$InteractionType interactionType;
    public String overviewSessionId;
    public ContentParcelables$Contents screenContents;
    public SuggestParcelables$Entity selectedEntity;
    public FeedbackParcelables$SelectionDetail selection;
    public int selectionPresentationMode;
    public String selectionSessionId;
    public String taskSnapshotSessionId;
    public SelectionType type;
    public SelectionInteraction userInteraction;

    public enum SelectionInteraction implements Parcelable {
        SELECTION_ACTION_UNKNOWN(0),
        SELECTION_INITIATED(1),
        SELECTION_DISMISSED(2),
        SELECTION_ADJUSTED(3),
        SELECTION_CONFIRMED(4),
        SELECTION_SUGGESTED(5),
        SELECTION_SUGGESTION_VERIFIED(6),
        SELECTION_SHOWN(7);
        
        public static final Creator<SelectionInteraction> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<SelectionInteraction>() {
                public SelectionInteraction createFromParcel(Parcel parcel) {
                    return SelectionInteraction.create(parcel);
                }

                public SelectionInteraction[] newArray(int i) {
                    return new SelectionInteraction[i];
                }
            };
        }

        private SelectionInteraction(int i) {
            this.value = i;
        }

        public static SelectionInteraction create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static SelectionInteraction create(int i) {
            if (i == 0) {
                return SELECTION_ACTION_UNKNOWN;
            }
            if (i == 1) {
                return SELECTION_INITIATED;
            }
            if (i == 2) {
                return SELECTION_DISMISSED;
            }
            if (i == 3) {
                return SELECTION_ADJUSTED;
            }
            if (i == 4) {
                return SELECTION_CONFIRMED;
            }
            if (i == 5) {
                return SELECTION_SUGGESTED;
            }
            if (i == 6) {
                return SELECTION_SUGGESTION_VERIFIED;
            }
            if (i == 7) {
                return SELECTION_SHOWN;
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

    public enum SelectionType implements Parcelable {
        SELECTION_TYPE_UNKNOWN(0),
        TEXT(1),
        IMAGE(2);
        
        public static final Creator<SelectionType> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<SelectionType>() {
                public SelectionType createFromParcel(Parcel parcel) {
                    return SelectionType.create(parcel);
                }

                public SelectionType[] newArray(int i) {
                    return new SelectionType[i];
                }
            };
        }

        private SelectionType(int i) {
            this.value = i;
        }

        public static SelectionType create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static SelectionType create(int i) {
            if (i == 0) {
                return SELECTION_TYPE_UNKNOWN;
            }
            if (i == 1) {
                return TEXT;
            }
            if (i == 2) {
                return IMAGE;
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

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$SelectionFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$SelectionFeedback() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.type == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.type.writeToParcel(parcel, i);
        }
        if (this.selectedEntity == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.selectedEntity.writeToParcel(parcel, i);
        }
        if (this.selection == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.selection.writeToParcel(parcel, i);
        }
        if (this.userInteraction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.userInteraction.writeToParcel(parcel, i);
        }
        parcel.writeInt(this.selectionPresentationMode);
        if (this.overviewSessionId == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.overviewSessionId);
        }
        if (this.taskSnapshotSessionId == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.taskSnapshotSessionId);
        }
        if (this.interactionSessionId == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.interactionSessionId);
        }
        if (this.selectionSessionId == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.selectionSessionId);
        }
        if (this.screenContents == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.screenContents.writeToParcel(parcel, i);
        }
        if (this.interactionType == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.interactionType.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.type = null;
        } else {
            this.type = (SelectionType) SelectionType.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.selectedEntity = null;
        } else {
            this.selectedEntity = (SuggestParcelables$Entity) SuggestParcelables$Entity.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.selection = null;
        } else {
            this.selection = (FeedbackParcelables$SelectionDetail) FeedbackParcelables$SelectionDetail.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.userInteraction = null;
        } else {
            this.userInteraction = (SelectionInteraction) SelectionInteraction.CREATOR.createFromParcel(parcel);
        }
        this.selectionPresentationMode = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.overviewSessionId = null;
        } else {
            this.overviewSessionId = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.taskSnapshotSessionId = null;
        } else {
            this.taskSnapshotSessionId = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.interactionSessionId = null;
        } else {
            this.interactionSessionId = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.selectionSessionId = null;
        } else {
            this.selectionSessionId = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.screenContents = null;
        } else {
            this.screenContents = (ContentParcelables$Contents) ContentParcelables$Contents.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.interactionType = null;
        } else {
            this.interactionType = (SuggestParcelables$InteractionType) SuggestParcelables$InteractionType.CREATOR.createFromParcel(parcel);
        }
    }
}
