package com.android.systemui.statusbar.phone;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import com.android.systemui.statusbar.policy.KeyButtonDrawable;

interface RotationButton {
    View getCurrentView();

    KeyButtonDrawable getImageDrawable();

    boolean hide();

    boolean isVisible();

    void setCanShowRotationButton(boolean z) {
    }

    void setDarkIntensity(float f);

    void setOnClickListener(OnClickListener onClickListener);

    void setOnHoverListener(OnHoverListener onHoverListener);

    void setRotationButtonController(RotationButtonController rotationButtonController);

    boolean show();

    void updateIcon();

    boolean acceptRotationProposal() {
        return getCurrentView() != null;
    }
}
