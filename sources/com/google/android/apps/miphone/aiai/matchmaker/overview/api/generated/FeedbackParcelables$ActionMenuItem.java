package com.google.android.apps.miphone.aiai.matchmaker.overview.api.generated;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class FeedbackParcelables$ActionMenuItem implements Parcelable {
    public static final Creator<FeedbackParcelables$ActionMenuItem> CREATOR = new Creator<FeedbackParcelables$ActionMenuItem>() {
        public FeedbackParcelables$ActionMenuItem createFromParcel(Parcel parcel) {
            return new FeedbackParcelables$ActionMenuItem(parcel);
        }

        public FeedbackParcelables$ActionMenuItem[] newArray(int i) {
            return new FeedbackParcelables$ActionMenuItem[i];
        }
    };
    public SuggestParcelables$IntentInfo actionIntent;
    public ActionMenuItemDisplayMode displayMode;
    public String displayName;

    /* renamed from: id */
    public String f90id;
    public int invokeRankIndex;

    public enum ActionMenuItemDisplayMode implements Parcelable {
        UNKNOWN_DISPLAY_MODE(0),
        ON_PRIMARY_MENU(1),
        ON_OVERFLOW_MENU(2);
        
        public static final Creator<ActionMenuItemDisplayMode> CREATOR = null;
        public final int value;

        public int describeContents() {
            return 0;
        }

        static {
            CREATOR = new Creator<ActionMenuItemDisplayMode>() {
                public ActionMenuItemDisplayMode createFromParcel(Parcel parcel) {
                    return ActionMenuItemDisplayMode.create(parcel);
                }

                public ActionMenuItemDisplayMode[] newArray(int i) {
                    return new ActionMenuItemDisplayMode[i];
                }
            };
        }

        private ActionMenuItemDisplayMode(int i) {
            this.value = i;
        }

        public static ActionMenuItemDisplayMode create(Parcel parcel) {
            return create(parcel.readInt());
        }

        public static ActionMenuItemDisplayMode create(int i) {
            if (i == 0) {
                return UNKNOWN_DISPLAY_MODE;
            }
            if (i == 1) {
                return ON_PRIMARY_MENU;
            }
            if (i == 2) {
                return ON_OVERFLOW_MENU;
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

    public FeedbackParcelables$ActionMenuItem(Parcel parcel) {
        readFromParcel(parcel);
    }

    private FeedbackParcelables$ActionMenuItem() {
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.f90id == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.f90id);
        }
        if (this.displayName == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            parcel.writeString(this.displayName);
        }
        parcel.writeInt(this.invokeRankIndex);
        if (this.displayMode == null) {
            parcel.writeByte(0);
        } else {
            parcel.writeByte(1);
            this.displayMode.writeToParcel(parcel, i);
        }
        if (this.actionIntent == null) {
            parcel.writeByte(0);
            return;
        }
        parcel.writeByte(1);
        this.actionIntent.writeToParcel(parcel, i);
    }

    private void readFromParcel(Parcel parcel) {
        if (parcel.readByte() == 0) {
            this.f90id = null;
        } else {
            this.f90id = parcel.readString();
        }
        if (parcel.readByte() == 0) {
            this.displayName = null;
        } else {
            this.displayName = parcel.readString();
        }
        this.invokeRankIndex = parcel.readInt();
        if (parcel.readByte() == 0) {
            this.displayMode = null;
        } else {
            this.displayMode = (ActionMenuItemDisplayMode) ActionMenuItemDisplayMode.CREATOR.createFromParcel(parcel);
        }
        if (parcel.readByte() == 0) {
            this.actionIntent = null;
        } else {
            this.actionIntent = (SuggestParcelables$IntentInfo) SuggestParcelables$IntentInfo.CREATOR.createFromParcel(parcel);
        }
    }
}
