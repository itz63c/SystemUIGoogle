package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.List;

public final class FeedbackParcelables$OverviewFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$OverviewFeedback> CREATOR = new Creator<FeedbackParcelables$OverviewFeedback>() {
        public FeedbackParcelables$OverviewFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$OverviewFeedback(parcel);
        }

        public FeedbackParcelables$OverviewFeedback[] newArray(int i) {
            return new FeedbackParcelables$OverviewFeedback[i];
        }
    };
    public int numSelectionsInitialized;
    public int numSelectionsSuggested;
    public int overviewPresentationMode;
    public String overviewSessionId;
    public String primaryTaskAppComponentName;
    public List<String> taskAppComponentNameList;
    public String taskSnapshotSessionId;
    public OverviewInteraction userInteraction;

    public enum OverviewInteraction implements Parcelable {
        UNKNOWN_OVERVIEW_ACTION(0),
        OVERVIEW_SCREEN_STARTED(1),
        OVERVIEW_SCREEN_DISMISSED(2),
        OVERVIEW_SCREEN_QUICK_DISMISSED(3),
        OVERVIEW_SCREEN_SWITCHED(4),
        OVERVIEW_TASK_SNAPSHOT_DISPLAY(5),
        OVERVIEW_SCREEN_APP_CLOSED(6),
        OVERVIEW_SCREEN_EXIT_APP_ENTERED(7),
        OVERVIEW_SCREEN_EXIT_BACK_BUTTON(8),
        OVERVIEW_SCREEN_EXIT_HOME_BUTTON(9),
        OVERVIEW_SCREEN_EXIT_POWER_BUTTON(10),
        OVERVIEW_SCREEN_ENTER_ALL_APPS(11);
        
        public static final Creator<OverviewInteraction> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<OverviewInteraction>() {
                public OverviewInteraction createFromParcel(Parcel parcel) {
                    return OverviewInteraction.create(parcel);
                }

                public OverviewInteraction[] newArray(int i) {
                    return new OverviewInteraction[i];
                }
            };
        }

        private OverviewInteraction(int i) {
            this.value = i;
        }

        public static OverviewInteraction create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static OverviewInteraction create(int i) {
            if (i == 0) {
                return UNKNOWN_OVERVIEW_ACTION;
            }
            if (i == 1) {
                return OVERVIEW_SCREEN_STARTED;
            }
            if (i == 2) {
                return OVERVIEW_SCREEN_DISMISSED;
            }
            if (i == 3) {
                return OVERVIEW_SCREEN_QUICK_DISMISSED;
            }
            if (i == 4) {
                return OVERVIEW_SCREEN_SWITCHED;
            }
            if (i == 5) {
                return OVERVIEW_TASK_SNAPSHOT_DISPLAY;
            }
            if (i == 6) {
                return OVERVIEW_SCREEN_APP_CLOSED;
            }
            if (i == 7) {
                return OVERVIEW_SCREEN_EXIT_APP_ENTERED;
            }
            if (i == 8) {
                return OVERVIEW_SCREEN_EXIT_BACK_BUTTON;
            }
            if (i == 9) {
                return OVERVIEW_SCREEN_EXIT_HOME_BUTTON;
            }
            if (i == 10) {
                return OVERVIEW_SCREEN_EXIT_POWER_BUTTON;
            }
            if (i == 11) {
                return OVERVIEW_SCREEN_ENTER_ALL_APPS;
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

    public FeedbackParcelables$OverviewFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$OverviewFeedback() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.userInteraction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.userInteraction.writeToParcel(parcel, i);
        }
        parcel.writeInt(this.overviewPresentationMode);
        parcel.writeInt(this.numSelectionsSuggested);
        parcel.writeInt(this.numSelectionsInitialized);
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
        if (this.primaryTaskAppComponentName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.primaryTaskAppComponentName);
        }
        if (this.taskAppComponentNameList == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeInt(this.taskAppComponentNameList.size());
        for (String str : this.taskAppComponentNameList) {
            if (str == null) {
                parcel.writeByte(0);
            } else {
                parcel.writeByte(1);
                parcel.writeString(str);
            }
        }
    }

    private void readFromParcel(Parcel parcel) {
        String str;
        if (parcel.readByte() == 0) {
            this.userInteraction = null;
        } else {
            this.userInteraction = (OverviewInteraction) OverviewInteraction.CREATOR.createFromParcel(parcel);
        }
        this.overviewPresentationMode = parcel.readInt();
        this.numSelectionsSuggested = parcel.readInt();
        this.numSelectionsInitialized = parcel.readInt();
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
            this.primaryTaskAppComponentName = null;
        } else {
            this.primaryTaskAppComponentName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.taskAppComponentNameList = null;
            return;
        }
        int readInt = parcel.readInt();
        String[] strArr = new String[readInt];
        for (int i = 0; i < readInt; i++) {
            if (parcel.readByte() == 0) {
                str = null;
            } else {
                str = parcel.readString();
            }
            strArr[i] = str;
        }
        this.taskAppComponentNameList = Arrays.asList(strArr);
    }
}
