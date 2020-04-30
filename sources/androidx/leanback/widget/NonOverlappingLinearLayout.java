package androidx.leanback.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import java.util.ArrayList;

public class NonOverlappingLinearLayout extends LinearLayout {
    boolean mDeferFocusableViewAvailableInLayout;
    boolean mFocusableViewAvailableFixEnabled;
    final ArrayList<ArrayList<View>> mSortedAvailableViews;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public NonOverlappingLinearLayout(Context context) {
        this(context, null);
    }

    public NonOverlappingLinearLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public NonOverlappingLinearLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mFocusableViewAvailableFixEnabled = false;
        this.mSortedAvailableViews = new ArrayList<>();
    }

    public void setFocusableViewAvailableFixEnabled(boolean z) {
        this.mFocusableViewAvailableFixEnabled = z;
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = 0;
        try {
            boolean z2 = this.mFocusableViewAvailableFixEnabled && getOrientation() == 0 && getLayoutDirection() == 1;
            this.mDeferFocusableViewAvailableInLayout = z2;
            if (z2) {
                while (this.mSortedAvailableViews.size() > getChildCount()) {
                    this.mSortedAvailableViews.remove(this.mSortedAvailableViews.size() - 1);
                }
                while (this.mSortedAvailableViews.size() < getChildCount()) {
                    this.mSortedAvailableViews.add(new ArrayList());
                }
            }
            super.onLayout(z, i, i2, i3, i4);
            if (this.mDeferFocusableViewAvailableInLayout) {
                for (int i6 = 0; i6 < this.mSortedAvailableViews.size(); i6++) {
                    for (int i7 = 0; i7 < ((ArrayList) this.mSortedAvailableViews.get(i6)).size(); i7++) {
                        super.focusableViewAvailable((View) ((ArrayList) this.mSortedAvailableViews.get(i6)).get(i7));
                    }
                }
            }
            if (this.mDeferFocusableViewAvailableInLayout) {
                this.mDeferFocusableViewAvailableInLayout = false;
                while (i5 < this.mSortedAvailableViews.size()) {
                    ((ArrayList) this.mSortedAvailableViews.get(i5)).clear();
                    i5++;
                }
            }
        } catch (Throwable th) {
            if (this.mDeferFocusableViewAvailableInLayout) {
                this.mDeferFocusableViewAvailableInLayout = false;
                while (i5 < this.mSortedAvailableViews.size()) {
                    ((ArrayList) this.mSortedAvailableViews.get(i5)).clear();
                    i5++;
                }
            }
            throw th;
        }
    }

    public void focusableViewAvailable(View view) {
        int i;
        if (this.mDeferFocusableViewAvailableInLayout) {
            View view2 = view;
            while (true) {
                if (view2 == this || view2 == null) {
                    i = -1;
                } else if (view2.getParent() == this) {
                    i = indexOfChild(view2);
                    break;
                } else {
                    view2 = (View) view2.getParent();
                }
            }
            if (i != -1) {
                ((ArrayList) this.mSortedAvailableViews.get(i)).add(view);
                return;
            }
            return;
        }
        super.focusableViewAvailable(view);
    }
}
