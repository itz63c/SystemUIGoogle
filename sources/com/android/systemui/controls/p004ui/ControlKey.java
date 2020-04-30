package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlKey */
/* compiled from: ControlsUiControllerImpl.kt */
final class ControlKey {
    private final ComponentName componentName;
    private final String controlId;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.controlId, (java.lang.Object) r3.controlId) != false) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x001f
            boolean r0 = r3 instanceof com.android.systemui.controls.p004ui.ControlKey
            if (r0 == 0) goto L_0x001d
            com.android.systemui.controls.ui.ControlKey r3 = (com.android.systemui.controls.p004ui.ControlKey) r3
            android.content.ComponentName r0 = r2.componentName
            android.content.ComponentName r1 = r3.componentName
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x001d
            java.lang.String r2 = r2.controlId
            java.lang.String r3 = r3.controlId
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x001d
            goto L_0x001f
        L_0x001d:
            r2 = 0
            return r2
        L_0x001f:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlKey.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        ComponentName componentName2 = this.componentName;
        int i = 0;
        int hashCode = (componentName2 != null ? componentName2.hashCode() : 0) * 31;
        String str = this.controlId;
        if (str != null) {
            i = str.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ControlKey(componentName=");
        sb.append(this.componentName);
        sb.append(", controlId=");
        sb.append(this.controlId);
        sb.append(")");
        return sb.toString();
    }

    public ControlKey(ComponentName componentName2, String str) {
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        this.componentName = componentName2;
        this.controlId = str;
    }
}
