package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$SelectionFeedback.SelectionType;
import java.util.Arrays;
import java.util.List;

public final class FeedbackParcelables$ActionGroupFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$ActionGroupFeedback> CREATOR = new Creator<FeedbackParcelables$ActionGroupFeedback>() {
        public FeedbackParcelables$ActionGroupFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$ActionGroupFeedback(parcel);
        }

        public FeedbackParcelables$ActionGroupFeedback[] newArray(int i) {
            return new FeedbackParcelables$ActionGroupFeedback[i];
        }
    };
    public SuggestParcelables$ActionGroup actionGroup;
    public int actionGroupPresentationMode;
    public List<SuggestParcelables$ActionGroup> actionGroupShown;
    public SuggestParcelables$Entity selectedEntity;
    public FeedbackParcelables$SelectionDetail selection;
    public SelectionType selectionType;
    public ActionGroupInteraction userInteraction;

    public enum ActionGroupInteraction implements Parcelable {
        ACTION_GROUP_ACTION_UNKNOWN(0),
        ACTION_GROUP_SHOWN(1),
        ACTION_GROUP_DISMISSED(2),
        ACTION_GROUP_EXPANDED(3);
        
        public static final Creator<ActionGroupInteraction> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<ActionGroupInteraction>() {
                public ActionGroupInteraction createFromParcel(Parcel parcel) {
                    return ActionGroupInteraction.create(parcel);
                }

                public ActionGroupInteraction[] newArray(int i) {
                    return new ActionGroupInteraction[i];
                }
            };
        }

        private ActionGroupInteraction(int i) {
            this.value = i;
        }

        public static ActionGroupInteraction create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static ActionGroupInteraction create(int i) {
            if (i == 0) {
                return ACTION_GROUP_ACTION_UNKNOWN;
            }
            if (i == 1) {
                return ACTION_GROUP_SHOWN;
            }
            if (i == 2) {
                return ACTION_GROUP_DISMISSED;
            }
            if (i == 3) {
                return ACTION_GROUP_EXPANDED;
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

    public FeedbackParcelables$ActionGroupFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$ActionGroupFeedback() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.selectionType == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.selectionType.writeToParcel(parcel, i);
        }
        if (this.selectedEntity == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.selectedEntity.writeToParcel(parcel, i);
        }
        if (this.actionGroupShown == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.actionGroupShown.size());
            for (SuggestParcelables$ActionGroup suggestParcelables$ActionGroup : this.actionGroupShown) {
                if (suggestParcelables$ActionGroup == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$ActionGroup.writeToParcel(parcel, i);
                }
            }
        }
        if (this.actionGroup == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.actionGroup.writeToParcel(parcel, i);
        }
        if (this.userInteraction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.userInteraction.writeToParcel(parcel, i);
        }
        parcel.writeInt(this.actionGroupPresentationMode);
        if (this.selection == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.selection.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        SuggestParcelables$ActionGroup suggestParcelables$ActionGroup;
        if (parcel.readByte() == 0) {
            this.selectionType = null;
        } else {
            this.selectionType = (SelectionType) SelectionType.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.selectedEntity = null;
        } else {
            this.selectedEntity = (SuggestParcelables$Entity) SuggestParcelables$Entity.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.actionGroupShown = null;
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
            this.actionGroupShown = Arrays.asList(suggestParcelables$ActionGroupArr);
        }
        if (parcel.readByte() == 0) {
            this.actionGroup = null;
        } else {
            this.actionGroup = (SuggestParcelables$ActionGroup) SuggestParcelables$ActionGroup.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.userInteraction = null;
        } else {
            this.userInteraction = (ActionGroupInteraction) ActionGroupInteraction.CREATOR.createFromParcel(parcel);
        }
        this.actionGroupPresentationMode = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.selection = null;
        } else {
            this.selection = (FeedbackParcelables$SelectionDetail) FeedbackParcelables$SelectionDetail.CREATOR.createFromParcel(parcel);
        }
    }
}
