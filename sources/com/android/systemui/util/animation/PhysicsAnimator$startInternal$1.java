package com.android.systemui.util.animation;

import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.util.animation.PhysicsAnimator.FlingConfig;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: PhysicsAnimator.kt */
final class PhysicsAnimator$startInternal$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ FloatPropertyCompat $animatedProperty;
    final /* synthetic */ float $currentValue;
    final /* synthetic */ FlingConfig $flingConfig;
    final /* synthetic */ PhysicsAnimator this$0;

    PhysicsAnimator$startInternal$1(PhysicsAnimator physicsAnimator, FlingConfig flingConfig, float f, FloatPropertyCompat floatPropertyCompat) {
        this.this$0 = physicsAnimator;
        this.$flingConfig = flingConfig;
        this.$currentValue = f;
        this.$animatedProperty = floatPropertyCompat;
        super(0);
    }

    public final void invoke() {
        FlingConfig flingConfig = this.$flingConfig;
        flingConfig.mo19423xdb5fd7d6(Math.min(this.$currentValue, flingConfig.mo19420x9e142a62()));
        flingConfig.mo19422x818c6de8(Math.max(this.$currentValue, flingConfig.mo19419x4440c074()));
        this.this$0.cancel(this.$animatedProperty);
        FlingAnimation access$getFlingAnimation = this.this$0.getFlingAnimation(this.$animatedProperty);
        this.$flingConfig.mo19415xe32feec1(access$getFlingAnimation);
        access$getFlingAnimation.start();
    }
}
