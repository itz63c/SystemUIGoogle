package com.android.systemui.bubbles.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener;

public class OneTimeEndListener implements OnAnimationEndListener {
    public void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        dynamicAnimation.removeEndListener(this);
    }
}
