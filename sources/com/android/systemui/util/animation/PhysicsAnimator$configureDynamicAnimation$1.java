package com.android.systemui.util.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.util.animation.PhysicsAnimator.InternalListener;

/* compiled from: PhysicsAnimator.kt */
final class PhysicsAnimator$configureDynamicAnimation$1 implements OnAnimationUpdateListener {
    final /* synthetic */ FloatPropertyCompat $property;
    final /* synthetic */ PhysicsAnimator this$0;

    PhysicsAnimator$configureDynamicAnimation$1(PhysicsAnimator physicsAnimator, FloatPropertyCompat floatPropertyCompat) {
        this.this$0 = physicsAnimator;
        this.$property = floatPropertyCompat;
    }

    public final void onAnimationUpdate(DynamicAnimation<DynamicAnimation<?>> dynamicAnimation, float f, float f2) {
        int size = this.this$0.mo19397x7fa0b292().size();
        for (int i = 0; i < size; i++) {
            ((InternalListener) this.this$0.mo19397x7fa0b292().get(i)).mo19427xa29f54d7(this.$property, f, f2);
        }
    }
}
