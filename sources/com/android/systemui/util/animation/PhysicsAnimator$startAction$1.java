package com.android.systemui.util.animation;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;

/* compiled from: PhysicsAnimator.kt */
final /* synthetic */ class PhysicsAnimator$startAction$1 extends FunctionReference implements Function0<Unit> {
    PhysicsAnimator$startAction$1(PhysicsAnimator physicsAnimator) {
        super(0, physicsAnimator);
    }

    public final String getName() {
        return "startInternal";
    }

    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(PhysicsAnimator.class);
    }

    public final String getSignature() {
        return "startInternal$frameworks__base__packages__SystemUI__android_common__SystemUI_core()V";
    }

    public final void invoke() {
        ((PhysicsAnimator) this.receiver).mo19404x3d1adf05();
    }
}
