package androidx.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;

class PersistentFocusWrapper extends FrameLayout {
    private boolean mPersistFocusVertical = true;
    private int mSelectedPosition = -1;

    static class SavedState extends BaseSavedState {
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        int mSelectedPosition;

        SavedState(Parcel parcel) {
            super(parcel);
            this.mSelectedPosition = parcel.readInt();
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.mSelectedPosition);
        }
    }

    public PersistentFocusWrapper(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: 0000 */
    public int getGrandChildCount() {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup == null) {
            return 0;
        }
        return viewGroup.getChildCount();
    }

    private boolean shouldPersistFocusFromDirection(int i) {
        return (this.mPersistFocusVertical && (i == 33 || i == 130)) || (!this.mPersistFocusVertical && (i == 17 || i == 66));
    }

    public void addFocusables(ArrayList<View> arrayList, int i, int i2) {
        if (hasFocus() || getGrandChildCount() == 0 || !shouldPersistFocusFromDirection(i)) {
            super.addFocusables(arrayList, i, i2);
        } else {
            arrayList.add(this);
        }
    }

    public void requestChildFocus(View view, View view2) {
        int i;
        super.requestChildFocus(view, view2);
        while (view2 != null && view2.getParent() != view) {
            view2 = (View) view2.getParent();
        }
        if (view2 == null) {
            i = -1;
        } else {
            i = ((ViewGroup) view).indexOfChild(view2);
        }
        this.mSelectedPosition = i;
    }

    public boolean requestFocus(int i, Rect rect) {
        ViewGroup viewGroup = (ViewGroup) getChildAt(0);
        if (viewGroup != null) {
            int i2 = this.mSelectedPosition;
            if (i2 >= 0 && i2 < getGrandChildCount() && viewGroup.getChildAt(this.mSelectedPosition).requestFocus(i, rect)) {
                return true;
            }
        }
        return super.requestFocus(i, rect);
    }

    /* access modifiers changed from: protected */
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.mSelectedPosition = this.mSelectedPosition;
        return savedState;
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        this.mSelectedPosition = savedState.mSelectedPosition;
        super.onRestoreInstanceState(savedState.getSuperState());
    }
}
