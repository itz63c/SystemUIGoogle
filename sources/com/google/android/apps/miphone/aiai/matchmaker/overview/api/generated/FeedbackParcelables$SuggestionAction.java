package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public enum FeedbackParcelables$SuggestionAction implements Parcelable {
    SUGGESTION_ACTION_UNKNOWN(0),
    SUGGESTION_ACTION_CLICKED(1),
    SUGGESTION_ACTION_DISMISSED(2),
    SUGGESTION_ACTION_SHOWN(3),
    SUGGESTION_ACTION_EXPANDED(4);
    
    public static final Creator<FeedbackParcelables$SuggestionAction> CREATOR = null;
    public final int value;

    public int describeContents() {
        return 0;
    }

    static {
        CREATOR = new Creator<FeedbackParcelables$SuggestionAction>() {
            public FeedbackParcelables$SuggestionAction createFromParcel(Parcel parcel) {
                return FeedbackParcelables$SuggestionAction.create(parcel);
            }

            public FeedbackParcelables$SuggestionAction[] newArray(int i) {
                return new FeedbackParcelables$SuggestionAction[i];
            }
        };
    }

    private FeedbackParcelables$SuggestionAction(int i) {
        this.value = i;
    }

    public static FeedbackParcelables$SuggestionAction create(Parcel parcel) {
        return create(parcel.readInt());
    }

    public static FeedbackParcelables$SuggestionAction create(int i) {
        if (i == 0) {
            return SUGGESTION_ACTION_UNKNOWN;
        }
        if (i == 1) {
            return SUGGESTION_ACTION_CLICKED;
        }
        if (i == 2) {
            return SUGGESTION_ACTION_DISMISSED;
        }
        if (i == 3) {
            return SUGGESTION_ACTION_SHOWN;
        }
        if (i == 4) {
            return SUGGESTION_ACTION_EXPANDED;
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
