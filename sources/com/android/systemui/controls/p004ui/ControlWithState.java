package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.service.controls.Control;
import com.android.systemui.controls.controller.ControlInfo;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.ControlWithState */
/* compiled from: ControlWithState.kt */
public final class ControlWithState {

    /* renamed from: ci */
    private final ControlInfo f44ci;
    private final ComponentName componentName;
    private final Control control;

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0024, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.control, (java.lang.Object) r3.control) != false) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0029
            boolean r0 = r3 instanceof com.android.systemui.controls.p004ui.ControlWithState
            if (r0 == 0) goto L_0x0027
            com.android.systemui.controls.ui.ControlWithState r3 = (com.android.systemui.controls.p004ui.ControlWithState) r3
            android.content.ComponentName r0 = r2.componentName
            android.content.ComponentName r1 = r3.componentName
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0027
            com.android.systemui.controls.controller.ControlInfo r0 = r2.f44ci
            com.android.systemui.controls.controller.ControlInfo r1 = r3.f44ci
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0027
            android.service.controls.Control r2 = r2.control
            android.service.controls.Control r3 = r3.control
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x0027
            goto L_0x0029
        L_0x0027:
            r2 = 0
            return r2
        L_0x0029:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.ControlWithState.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        ComponentName componentName2 = this.componentName;
        int i = 0;
        int hashCode = (componentName2 != null ? componentName2.hashCode() : 0) * 31;
        ControlInfo controlInfo = this.f44ci;
        int hashCode2 = (hashCode + (controlInfo != null ? controlInfo.hashCode() : 0)) * 31;
        Control control2 = this.control;
        if (control2 != null) {
            i = control2.hashCode();
        }
        return hashCode2 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ControlWithState(componentName=");
        sb.append(this.componentName);
        sb.append(", ci=");
        sb.append(this.f44ci);
        sb.append(", control=");
        sb.append(this.control);
        sb.append(")");
        return sb.toString();
    }

    public ControlWithState(ComponentName componentName2, ControlInfo controlInfo, Control control2) {
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        Intrinsics.checkParameterIsNotNull(controlInfo, "ci");
        this.componentName = componentName2;
        this.f44ci = controlInfo;
        this.control = control2;
    }

    public final ComponentName getComponentName() {
        return this.componentName;
    }

    public final ControlInfo getCi() {
        return this.f44ci;
    }

    public final Control getControl() {
        return this.control;
    }
}
