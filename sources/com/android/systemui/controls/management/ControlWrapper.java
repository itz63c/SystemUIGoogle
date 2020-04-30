package com.android.systemui.controls.management;

import com.android.systemui.controls.ControlStatus;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsModel.kt */
public final class ControlWrapper extends ElementWrapper {
    private final ControlStatus controlStatus;

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0010, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1.controlStatus, (java.lang.Object) ((com.android.systemui.controls.management.ControlWrapper) r2).controlStatus) != false) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r2) {
        /*
            r1 = this;
            if (r1 == r2) goto L_0x0015
            boolean r0 = r2 instanceof com.android.systemui.controls.management.ControlWrapper
            if (r0 == 0) goto L_0x0013
            com.android.systemui.controls.management.ControlWrapper r2 = (com.android.systemui.controls.management.ControlWrapper) r2
            com.android.systemui.controls.ControlStatus r1 = r1.controlStatus
            com.android.systemui.controls.ControlStatus r2 = r2.controlStatus
            boolean r1 = kotlin.jvm.internal.Intrinsics.areEqual(r1, r2)
            if (r1 == 0) goto L_0x0013
            goto L_0x0015
        L_0x0013:
            r1 = 0
            return r1
        L_0x0015:
            r1 = 1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.management.ControlWrapper.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        ControlStatus controlStatus2 = this.controlStatus;
        if (controlStatus2 != null) {
            return controlStatus2.hashCode();
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ControlWrapper(controlStatus=");
        sb.append(this.controlStatus);
        sb.append(")");
        return sb.toString();
    }

    public ControlWrapper(ControlStatus controlStatus2) {
        Intrinsics.checkParameterIsNotNull(controlStatus2, "controlStatus");
        super(null);
        this.controlStatus = controlStatus2;
    }

    public final ControlStatus getControlStatus() {
        return this.controlStatus;
    }
}
