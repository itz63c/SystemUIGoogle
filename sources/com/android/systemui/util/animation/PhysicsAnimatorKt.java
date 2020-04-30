package com.android.systemui.util.animation;

import com.android.systemui.util.animation.PhysicsAnimator.FlingConfig;
import com.android.systemui.util.animation.PhysicsAnimator.SpringConfig;
import java.util.WeakHashMap;
import kotlin.jvm.internal.FloatCompanionObject;

/* compiled from: PhysicsAnimator.kt */
public final class PhysicsAnimatorKt {
    /* access modifiers changed from: private */
    public static final float UNSET = (-FloatCompanionObject.INSTANCE.getMAX_VALUE());
    private static final WeakHashMap<Object, PhysicsAnimator<?>> animators = new WeakHashMap<>();
    /* access modifiers changed from: private */
    public static final FlingConfig defaultFling = new FlingConfig(1.0f, -FloatCompanionObject.INSTANCE.getMAX_VALUE(), FloatCompanionObject.INSTANCE.getMAX_VALUE());
    /* access modifiers changed from: private */
    public static final SpringConfig defaultSpring = new SpringConfig(1500.0f, 0.5f);
    /* access modifiers changed from: private */
    public static boolean verboseLogging;

    public static final WeakHashMap<Object, PhysicsAnimator<?>> getAnimators() {
        return animators;
    }
}
