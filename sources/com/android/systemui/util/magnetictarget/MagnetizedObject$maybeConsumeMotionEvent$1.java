package com.android.systemui.util.magnetictarget;

import com.android.systemui.util.magnetictarget.MagnetizedObject.MagneticTarget;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Lambda;

/* compiled from: MagnetizedObject.kt */
final class MagnetizedObject$maybeConsumeMotionEvent$1 extends Lambda implements Function0<Unit> {
    final /* synthetic */ MagneticTarget $flungToTarget;
    final /* synthetic */ MagnetizedObject this$0;

    MagnetizedObject$maybeConsumeMotionEvent$1(MagnetizedObject magnetizedObject, MagneticTarget magneticTarget) {
        this.this$0 = magnetizedObject;
        this.$flungToTarget = magneticTarget;
        super(0);
    }

    public final void invoke() {
        this.this$0.targetObjectIsStuckTo = null;
        this.this$0.getMagnetListener().onReleasedInTarget(this.$flungToTarget);
        this.this$0.vibrateIfEnabled(5);
    }
}
