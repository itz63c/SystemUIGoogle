package com.android.systemui.util.animation;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import com.android.systemui.util.animation.PhysicsAnimator.InternalListener;
import java.util.List;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PhysicsAnimator.kt */
final class PhysicsAnimator$configureDynamicAnimation$2 implements OnAnimationEndListener {
    final /* synthetic */ DynamicAnimation $anim;
    final /* synthetic */ FloatPropertyCompat $property;
    final /* synthetic */ PhysicsAnimator this$0;

    PhysicsAnimator$configureDynamicAnimation$2(PhysicsAnimator physicsAnimator, FloatPropertyCompat floatPropertyCompat, DynamicAnimation dynamicAnimation) {
        this.this$0 = physicsAnimator;
        this.$property = floatPropertyCompat;
        this.$anim = dynamicAnimation;
    }

    public final void onAnimationEnd(DynamicAnimation<DynamicAnimation<?>> dynamicAnimation, final boolean z, final float f, final float f2) {
        CollectionsKt__MutableCollectionsKt.removeAll((List) this.this$0.mo19397x7fa0b292(), (Function1) new Function1<InternalListener, Boolean>(this) {
            final /* synthetic */ PhysicsAnimator$configureDynamicAnimation$2 this$0;

            {
                this.this$0 = r1;
            }

            public /* bridge */ /* synthetic */ Object invoke(Object obj) {
                return Boolean.valueOf(invoke((InternalListener) obj));
            }

            public final boolean invoke(InternalListener internalListener) {
                Intrinsics.checkParameterIsNotNull(internalListener, "it");
                PhysicsAnimator$configureDynamicAnimation$2 physicsAnimator$configureDynamicAnimation$2 = this.this$0;
                return internalListener.mo19426x1484a259(physicsAnimator$configureDynamicAnimation$2.$property, z, f, f2, physicsAnimator$configureDynamicAnimation$2.$anim instanceof FlingAnimation);
            }
        });
    }
}
