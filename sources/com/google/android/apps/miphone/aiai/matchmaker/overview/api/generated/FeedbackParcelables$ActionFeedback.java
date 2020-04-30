package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated.FeedbackParcelables$SelectionFeedback.SelectionType;
import java.util.Arrays;
import java.util.List;

public final class FeedbackParcelables$ActionFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$ActionFeedback> CREATOR = new Creator<FeedbackParcelables$ActionFeedback>() {
        public FeedbackParcelables$ActionFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$ActionFeedback(parcel);
        }

        public FeedbackParcelables$ActionFeedback[] newArray(int i) {
            return new FeedbackParcelables$ActionFeedback[i];
        }
    };
    public List<FeedbackParcelables$ActionMenuItem> actionMenuItems;
    public int actionPresentationMode;
    public List<SuggestParcelables$Action> actionShown;
    public String interactionSessionId;
    public SuggestParcelables$InteractionType interactionType;
    public SuggestParcelables$Action invokedAction;
    public FeedbackParcelables$ActionMenuItem invokedActionMenuItem;
    public String overviewSessionId;
    public SuggestParcelables$Entity selectedEntity;
    public FeedbackParcelables$SelectionDetail selection;
    public String selectionSessionId;
    public SelectionType selectionType;
    public String taskSnapshotSessionId;
    public ActionInteraction userInteraction;
    public String verticalTypeName;

    public enum ActionInteraction implements Parcelable {
        ACTION_UNKNOWN(0),
        ACTION_SHOWN(1),
        ACTION_INVOKED(2),
        ACTION_DISMISSED(3),
        ACTION_MENU_SHOWN(4);
        
        public static final Creator<ActionInteraction> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<ActionInteraction>() {
                public ActionInteraction createFromParcel(Parcel parcel) {
                    return ActionInteraction.create(parcel);
                }

                public ActionInteraction[] newArray(int i) {
                    return new ActionInteraction[i];
                }
            };
        }

        private ActionInteraction(int i) {
            this.value = i;
        }

        public static ActionInteraction create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static ActionInteraction create(int i) {
            if (i == 0) {
                return ACTION_UNKNOWN;
            }
            if (i == 1) {
                return ACTION_SHOWN;
            }
            if (i == 2) {
                return ACTION_INVOKED;
            }
            if (i == 3) {
                return ACTION_DISMISSED;
            }
            if (i == 4) {
                return ACTION_MENU_SHOWN;
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

    public FeedbackParcelables$ActionFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$ActionFeedback() {
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
        if (this.actionShown == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.actionShown.size());
            for (SuggestParcelables$Action suggestParcelables$Action : this.actionShown) {
                if (suggestParcelables$Action == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    suggestParcelables$Action.writeToParcel(parcel, i);
                }
            }
        }
        if (this.invokedAction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.invokedAction.writeToParcel(parcel, i);
        }
        if (this.userInteraction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.userInteraction.writeToParcel(parcel, i);
        }
        parcel.writeInt(this.actionPresentationMode);
        if (this.selection == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.selection.writeToParcel(parcel, i);
        }
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
        if (this.verticalTypeName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.verticalTypeName);
        }
        if (this.actionMenuItems == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.actionMenuItems.size());
            for (FeedbackParcelables$ActionMenuItem feedbackParcelables$ActionMenuItem : this.actionMenuItems) {
                if (feedbackParcelables$ActionMenuItem == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    feedbackParcelables$ActionMenuItem.writeToParcel(parcel, i);
                }
            }
        }
        if (this.invokedActionMenuItem == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.invokedActionMenuItem.writeToParcel(parcel, i);
        }
        if (this.interactionType == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.interactionType.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        FeedbackParcelables$ActionMenuItem feedbackParcelables$ActionMenuItem;
        SuggestParcelables$Action suggestParcelables$Action;
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
            this.actionShown = null;
        } else {
            int readInt = parcel.readInt();
            SuggestParcelables$Action[] suggestParcelables$ActionArr = new SuggestParcelables$Action[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    suggestParcelables$Action = null;
                } else {
                    suggestParcelables$Action = (SuggestParcelables$Action) SuggestParcelables$Action.CREATOR.createFromParcel(parcel);
                }
                suggestParcelables$ActionArr[i] = suggestParcelables$Action;
            }
            this.actionShown = Arrays.asList(suggestParcelables$ActionArr);
        }
        if (parcel.readByte() == 0) {
            this.invokedAction = null;
        } else {
            this.invokedAction = (SuggestParcelables$Action) SuggestParcelables$Action.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.userInteraction = null;
        } else {
            this.userInteraction = (ActionInteraction) ActionInteraction.CREATOR.createFromParcel(parcel);
        }
        this.actionPresentationMode = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.selection = null;
        } else {
            this.selection = (FeedbackParcelables$SelectionDetail) FeedbackParcelables$SelectionDetail.CREATOR.createFromParcel(parcel);
        }
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
            this.verticalTypeName = null;
        } else {
            this.verticalTypeName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.actionMenuItems = null;
        } else {
            int readInt2 = parcel.readInt();
            FeedbackParcelables$ActionMenuItem[] feedbackParcelables$ActionMenuItemArr = new FeedbackParcelables$ActionMenuItem[readInt2];
            for (int i2 = 0; i2 < readInt2; i2++) {
                if (parcel.readByte() == 0) {
                    feedbackParcelables$ActionMenuItem = null;
                } else {
                    feedbackParcelables$ActionMenuItem = (FeedbackParcelables$ActionMenuItem) FeedbackParcelables$ActionMenuItem.CREATOR.createFromParcel(parcel);
                }
                feedbackParcelables$ActionMenuItemArr[i2] = feedbackParcelables$ActionMenuItem;
            }
            this.actionMenuItems = Arrays.asList(feedbackParcelables$ActionMenuItemArr);
        }
        if (parcel.readByte() == 0) {
            this.invokedActionMenuItem = null;
        } else {
            this.invokedActionMenuItem = (FeedbackParcelables$ActionMenuItem) FeedbackParcelables$ActionMenuItem.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.interactionType = null;
        } else {
            this.interactionType = (SuggestParcelables$InteractionType) SuggestParcelables$InteractionType.CREATOR.createFromParcel(parcel);
        }
    }
}
