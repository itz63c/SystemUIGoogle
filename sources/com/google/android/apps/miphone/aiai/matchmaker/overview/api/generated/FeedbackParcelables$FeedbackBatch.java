package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class FeedbackParcelables$FeedbackBatch implements Parcelable {
    public static final Creator<FeedbackParcelables$FeedbackBatch> CREATOR = new Creator<FeedbackParcelables$FeedbackBatch>() {
        public FeedbackParcelables$FeedbackBatch createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$FeedbackBatch(parcel);
        }

        public FeedbackParcelables$FeedbackBatch[] newArray(int i) {
            return new FeedbackParcelables$FeedbackBatch[i];
        }
    };
    public List<FeedbackParcelables$Feedback> feedback;
    public String overviewSessionId;
    public long screenSessionId;

    public int describeContents() {
        return 0;
    }

    public FeedbackParcelables$FeedbackBatch(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$FeedbackBatch() {
    }

    public static FeedbackParcelables$FeedbackBatch create() {
        return new FeedbackParcelables$FeedbackBatch();
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.feedback == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeInt(this.feedback.size());
            for (FeedbackParcelables$Feedback feedbackParcelables$Feedback : this.feedback) {
                if (feedbackParcelables$Feedback == null) {
                    parcel.writeByte(0);
                } else {
                    parcel.writeByte(1);
                    feedbackParcelables$Feedback.writeToParcel(parcel, i);
                }
            }
        }
        parcel.writeLong(this.screenSessionId);
        if (this.overviewSessionId == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.overviewSessionId);
    }

    private void readFromParcel(Parcel parcel) {
        FeedbackParcelables$Feedback feedbackParcelables$Feedback;
        if (parcel.readByte() == 0) {
            this.feedback = null;
        } else {
            int readInt = parcel.readInt();
            FeedbackParcelables$Feedback[] feedbackParcelables$FeedbackArr = new FeedbackParcelables$Feedback[readInt];
            for (int i = 0; i < readInt; i++) {
                if (parcel.readByte() == 0) {
                    feedbackParcelables$Feedback = null;
                } else {
                    feedbackParcelables$Feedback = (FeedbackParcelables$Feedback) FeedbackParcelables$Feedback.CREATOR.createFromParcel(parcel);
                }
                feedbackParcelables$FeedbackArr[i] = feedbackParcelables$Feedback;
            }
            this.feedback = Arrays.asList(feedbackParcelables$FeedbackArr);
        }
        this.screenSessionId = parcel.readLong();
        if (parcel.readByte() == 0) {
            this.overviewSessionId = null;
        } else {
            this.overviewSessionId = parcel.readString();
        }
    }
}
