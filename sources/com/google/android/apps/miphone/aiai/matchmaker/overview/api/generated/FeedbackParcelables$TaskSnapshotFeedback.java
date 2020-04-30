package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$TaskSnapshotFeedback implements Parcelable {
    public static final Creator<FeedbackParcelables$TaskSnapshotFeedback> CREATOR = new Creator<FeedbackParcelables$TaskSnapshotFeedback>() {
        public FeedbackParcelables$TaskSnapshotFeedback createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$TaskSnapshotFeedback(parcel);
        }

        public FeedbackParcelables$TaskSnapshotFeedback[] newArray(int i) {
            return new FeedbackParcelables$TaskSnapshotFeedback[i];
        }
    };
    public String interactionSessionId;
    public String overviewSessionId;
    public String taskAppComponentName;
    public String taskSnapshotSessionId;
    public TaskSnapshotInteraction userInteraction;

    public enum TaskSnapshotInteraction implements Parcelable {
        UNKNOWN_TASK_SNAPSHOT_ACTION(0),
        TASK_SNAPSHOT_CREATED(1),
        TASK_SNAPSHOT_SUGGEST_VIEW_DISPLAYED(2),
        TASK_SNAPSHOT_PROACTIVE_HINTS_DISPLAYED(3),
        TASK_SNAPSHOT_GLEAMS_DISPLAYED(4),
        TASK_SNAPSHOT_LONG_PRESSED(5),
        TASK_SNAPSHOT_DISMISSED(6);
        
        public static final Creator<TaskSnapshotInteraction> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<TaskSnapshotInteraction>() {
                public TaskSnapshotInteraction createFromParcel(Parcel parcel) {
                    return TaskSnapshotInteraction.create(parcel);
                }

                public TaskSnapshotInteraction[] newArray(int i) {
                    return new TaskSnapshotInteraction[i];
                }
            };
        }

        private TaskSnapshotInteraction(int i) {
            this.value = i;
        }

        public static TaskSnapshotInteraction create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static TaskSnapshotInteraction create(int i) {
            if (i == 0) {
                return UNKNOWN_TASK_SNAPSHOT_ACTION;
            }
            if (i == 1) {
                return TASK_SNAPSHOT_CREATED;
            }
            if (i == 2) {
                return TASK_SNAPSHOT_SUGGEST_VIEW_DISPLAYED;
            }
            if (i == 3) {
                return TASK_SNAPSHOT_PROACTIVE_HINTS_DISPLAYED;
            }
            if (i == 4) {
                return TASK_SNAPSHOT_GLEAMS_DISPLAYED;
            }
            if (i == 5) {
                return TASK_SNAPSHOT_LONG_PRESSED;
            }
            if (i == 6) {
                return TASK_SNAPSHOT_DISMISSED;
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

    public FeedbackParcelables$TaskSnapshotFeedback(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$TaskSnapshotFeedback() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.userInteraction == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.userInteraction.writeToParcel(parcel, i);
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
        if (this.taskAppComponentName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.taskAppComponentName);
        }
        if (this.interactionSessionId == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        parcel.writeString(this.interactionSessionId);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.userInteraction = null;
        } else {
            this.userInteraction = (TaskSnapshotInteraction) TaskSnapshotInteraction.CREATOR.createFromParcel(parcel);
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
            this.taskAppComponentName = null;
        } else {
            this.taskAppComponentName = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.interactionSessionId = null;
        } else {
            this.interactionSessionId = parcel.readString();
        }
    }
}
