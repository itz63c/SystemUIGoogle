package com.android.systemui.globalactions;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.C2009R$dimen;
import com.android.systemui.HardwareBgDrawable;

public class GlobalActionsFlatLayout extends GlobalActionsLayout {
    public float getAnimationOffsetX() {
        return 0.0f;
    }

    /* access modifiers changed from: protected */
    public HardwareBgDrawable getBackgroundDrawable(int i) {
        return null;
    }

    public GlobalActionsFlatLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean shouldReverseListItems() {
        int currentRotation = getCurrentRotation();
        boolean z = false;
        if (currentRotation == 0) {
            return false;
        }
        if (getCurrentLayoutDirection() == 1) {
            if (currentRotation == 1) {
                z = true;
            }
            return z;
        }
        if (currentRotation == 2) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: protected */
    public void addToListView(View view, boolean z) {
        if (getListView().getChildCount() < 4) {
            super.addToListView(view, z);
        }
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getGridItemSize() {
        return getContext().getResources().getDimension(C2009R$dimen.global_actions_grid_item_height);
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public float getAnimationDistance() {
        return getGridItemSize() / 2.0f;
    }

    public float getAnimationOffsetY() {
        return -getAnimationDistance();
    }
}
