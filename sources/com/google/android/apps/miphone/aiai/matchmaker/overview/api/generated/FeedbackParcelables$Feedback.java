package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$Feedback implements Parcelable {
    public static final Creator<FeedbackParcelables$Feedback> CREATOR = new Creator<FeedbackParcelables$Feedback>() {
        public FeedbackParcelables$Feedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$Feedback(parcel);
        }

        public FeedbackParcelables$Feedback[] newArray(int i) {
            return new FeedbackParcelables$Feedback[i];
        }
    };
    public Object feedback;

    /* renamed from: id */
    public String f91id;
    public InteractionContextParcelables$InteractionContext interactionContext;
    public FeedbackParcelables$SuggestionAction suggestionAction;
    public long timestampMs;

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$Feedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$Feedback() {
    }

    public static FeedbackParcelables$Feedback create() {
        return new FeedbackParcelables$Feedback();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.feedback == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            if (this.feedback instanceof FeedbackParcelables$OverviewFeedback) {
                parcel.writeInt(6);
                ((FeedbackParcelables$OverviewFeedback) this.feedback).writeToParcel(parcel, i);
            }
            if (this.feedback instanceof FeedbackParcelables$SelectionFeedback) {
                parcel.writeInt(7);
                ((FeedbackParcelables$SelectionFeedback) this.feedback).writeToParcel(parcel, i);
            }
            if (this.feedback instanceof FeedbackParcelables$ActionFeedback) {
                parcel.writeInt(8);
                ((FeedbackParcelables$ActionFeedback) this.feedback).writeToParcel(parcel, i);
            }
            if (this.feedback instanceof FeedbackParcelables$ActionGroupFeedback) {
                parcel.writeInt(9);
                ((FeedbackParcelables$ActionGroupFeedback) this.feedback).writeToParcel(parcel, i);
            }
            if (this.feedback instanceof FeedbackParcelables$TaskSnapshotFeedback) {
                parcel.writeInt(10);
                ((FeedbackParcelables$TaskSnapshotFeedback) this.feedback).writeToParcel(parcel, i);
            }
            if (this.feedback instanceof FeedbackParcelables$ScreenshotFeedback) {
                parcel.writeInt(11);
                ((FeedbackParcelables$ScreenshotFeedback) this.feedback).writeToParcel(parcel, i);
            }
        }
        if (this.f91id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f91id);
        }
        parcel.writeLong(this.timestampMs);
        if (this.suggestionAction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.suggestionAction.writeToParcel(parcel, i);
        }
        if (this.interactionContext == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.interactionContext.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.feedback = null;
        } else {
            int readInt = parcel.readInt();
            if (readInt == 6) {
                this.feedback = FeedbackParcelables$OverviewFeedback.CREATOR.createFromParcel(parcel);
            }
            if (readInt == 7) {
                this.feedback = FeedbackParcelables$SelectionFeedback.CREATOR.createFromParcel(parcel);
            }
            if (readInt == 8) {
                this.feedback = FeedbackParcelables$ActionFeedback.CREATOR.createFromParcel(parcel);
            }
            if (readInt == 9) {
                this.feedback = FeedbackParcelables$ActionGroupFeedback.CREATOR.createFromParcel(parcel);
            }
            if (readInt == 10) {
                this.feedback = FeedbackParcelables$TaskSnapshotFeedback.CREATOR.createFromParcel(parcel);
            }
            if (readInt == 11) {
                this.feedback = FeedbackParcelables$ScreenshotFeedback.CREATOR.createFromParcel(parcel);
            }
        }
        if (parcel.readByte() == 0) {
            this.f91id = null;
        } else {
            this.f91id = parcel.readString();
        }
        this.timestampMs = parcel.readLong();
        if (parcel.readByte() == 0) {
            this.suggestionAction = null;
        } else {
            this.suggestionAction = (FeedbackParcelables$SuggestionAction) FeedbackParcelables$SuggestionAction.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.interactionContext = null;
        } else {
            this.interactionContext = (InteractionContextParcelables$InteractionContext) InteractionContextParcelables$InteractionContext.CREATOR.createFromParcel(parcel);
        }
    }
}
