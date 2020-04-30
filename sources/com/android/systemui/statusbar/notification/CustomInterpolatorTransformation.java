package com.android.systemui.statusbar.notification;

import com.android.systemui.statusbar.CrossFadeHelper;
import com.android.systemui.statusbar.TransformableView;
import com.android.systemui.statusbar.ViewTransformationHelper.CustomTransformation;

public abstract class CustomInterpolatorTransformation extends CustomTransformation {
    private final int mViewType;

    /* access modifiers changed from: protected */
    public abstract boolean hasCustomTransformation();

    public CustomInterpolatorTransformation(int i) {
        this.mViewType = i;
    }

    public boolean transformTo(TransformState transformState, TransformableView transformableView, float f) {
        if (!hasCustomTransformation()) {
            return false;
        }
        TransformState currentState = transformableView.getCurrentState(this.mViewType);
        if (currentState == null) {
            return false;
        }
        CrossFadeHelper.fadeOut(transformState.getTransformedView(), f);
        transformState.transformViewFullyTo(currentState, this, f);
        currentState.recycle();
        return true;
    }

    public boolean transformFrom(TransformState transformState, TransformableView transformableView, float f) {
        if (!hasCustomTransformation()) {
            return false;
        }
        TransformState currentState = transformableView.getCurrentState(this.mViewType);
        if (currentState == null) {
            return false;
        }
        CrossFadeHelper.fadeIn(transformState.getTransformedView(), f);
        transformState.transformViewFullyFrom(currentState, this, f);
        currentState.recycle();
        return true;
    }
}
