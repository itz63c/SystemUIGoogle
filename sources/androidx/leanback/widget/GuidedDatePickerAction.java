package androidx.leanback.widget;

import android.os.Bundle;

public class GuidedDatePickerAction extends GuidedAction {
    long mDate;
    String mDatePickerFormat;
    long mMaxDate = Long.MAX_VALUE;
    long mMinDate = Long.MIN_VALUE;

    public String getDatePickerFormat() {
        return this.mDatePickerFormat;
    }

    public long getDate() {
        return this.mDate;
    }

    public void setDate(long j) {
        this.mDate = j;
    }

    public long getMinDate() {
        return this.mMinDate;
    }

    public long getMaxDate() {
        return this.mMaxDate;
    }

    public void onSaveInstanceState(Bundle bundle, String str) {
        bundle.putLong(str, getDate());
    }

    public void onRestoreInstanceState(Bundle bundle, String str) {
        setDate(bundle.getLong(str, getDate()));
    }
}
