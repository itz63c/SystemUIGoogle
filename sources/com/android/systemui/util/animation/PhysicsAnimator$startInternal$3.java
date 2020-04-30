package com.android.systemui.util.animation;

import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import com.android.systemui.util.animation.PhysicsAnimator.EndListener;
import com.android.systemui.util.animation.PhysicsAnimator.SpringConfig;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PhysicsAnimator.kt */
public final class PhysicsAnimator$startInternal$3 implements EndListener<T> {
    final /* synthetic */ FloatPropertyCompat $animatedProperty;
    final /* synthetic */ float $flingMax;
    final /* synthetic */ float $flingMin;
    final /* synthetic */ SpringConfig $springConfig;
    final /* synthetic */ PhysicsAnimator this$0;

    PhysicsAnimator$startInternal$3(PhysicsAnimator physicsAnimator, FloatPropertyCompat floatPropertyCompat, float f, float f2, SpringConfig springConfig) {
        this.this$0 = physicsAnimator;
        this.$animatedProperty = floatPropertyCompat;
        this.$flingMin = f;
        this.$flingMax = f2;
        this.$springConfig = springConfig;
    }

    public void onAnimationEnd(T t, FloatPropertyCompat<? super T> floatPropertyCompat, boolean z, boolean z2, float f, float f2, boolean z3) {
        Intrinsics.checkParameterIsNotNull(floatPropertyCompat, "property");
        boolean areEqual = Intrinsics.areEqual((Object) floatPropertyCompat, (Object) this.$animatedProperty);
        boolean z4 = true;
        if (!(!areEqual) && z && !z2) {
            float f3 = (float) 0;
            boolean z5 = Math.abs(f2) > f3;
            if (f >= this.$flingMin && f <= this.$flingMax) {
                z4 = false;
            }
            if (z5 || z4) {
                this.$springConfig.mo19436xa675e8e3(f2);
                if (this.$springConfig.mo19432xb78cd1cf() == PhysicsAnimatorKt.UNSET) {
                    if (z5) {
                        this.$springConfig.mo19435x17853e43(f2 < f3 ? this.$flingMin : this.$flingMax);
                    } else if (z4) {
                        SpringConfig springConfig = this.$springConfig;
                        float f4 = this.$flingMin;
                        if (f >= f4) {
                            f4 = this.$flingMax;
                        }
                        springConfig.mo19435x17853e43(f4);
                    }
                }
                SpringAnimation access$getSpringAnimation = this.this$0.getSpringAnimation(this.$animatedProperty);
                this.$springConfig.mo19428xe32feec1(access$getSpringAnimation);
                access$getSpringAnimation.start();
            }
        }
    }
}
