package com.android.systemui.statusbar.phone;

import android.content.Context;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;

public class ContextualButton extends ButtonDispatcher {
    private ContextualButtonGroup mGroup;
    protected final int mIconResId;
    private ContextButtonListener mListener;

    public interface ContextButtonListener {
        void onVisibilityChanged(ContextualButton contextualButton, boolean z);
    }

    public ContextualButton(int i, int i2) {
        super(i);
        this.mIconResId = i2;
    }

    public void updateIcon() {
        if (getCurrentView() != null && getCurrentView().isAttachedToWindow() && this.mIconResId != 0) {
            KeyButtonDrawable imageDrawable = getImageDrawable();
            KeyButtonDrawable newDrawable = getNewDrawable();
            if (imageDrawable != null) {
                newDrawable.setDarkIntensity(imageDrawable.getDarkIntensity());
            }
            setImageDrawable(newDrawable);
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        KeyButtonDrawable imageDrawable = getImageDrawable();
        if (!(i == 0 || imageDrawable == null || !imageDrawable.canAnimate())) {
            imageDrawable.clearAnimationCallbacks();
            imageDrawable.resetAnimation();
        }
        ContextButtonListener contextButtonListener = this.mListener;
        if (contextButtonListener != null) {
            contextButtonListener.onVisibilityChanged(this, i == 0);
        }
    }

    public void setListener(ContextButtonListener contextButtonListener) {
        this.mListener = contextButtonListener;
    }

    public boolean show() {
        ContextualButtonGroup contextualButtonGroup = this.mGroup;
        boolean z = false;
        if (contextualButtonGroup == null) {
            setVisibility(0);
            return true;
        }
        if (contextualButtonGroup.setButtonVisibility(getId(), true) == 0) {
            z = true;
        }
        return z;
    }

    public boolean hide() {
        ContextualButtonGroup contextualButtonGroup = this.mGroup;
        boolean z = false;
        if (contextualButtonGroup == null) {
            setVisibility(4);
            return false;
        }
        if (contextualButtonGroup.setButtonVisibility(getId(), false) != 0) {
            z = true;
        }
        return z;
    }

    /* access modifiers changed from: 0000 */
    public void attachToGroup(ContextualButtonGroup contextualButtonGroup) {
        this.mGroup = contextualButtonGroup;
    }

    /* access modifiers changed from: protected */
    public KeyButtonDrawable getNewDrawable() {
        return KeyButtonDrawable.create(getContext().getApplicationContext(), this.mIconResId, false);
    }

    /* access modifiers changed from: protected */
    public Context getContext() {
        return getCurrentView().getContext();
    }
}
