package com.android.systemui.controls;

import android.content.ComponentName;
import android.service.controls.Control;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlStatus.kt */
public final class ControlStatus {
    private final ComponentName component;
    private final Control control;
    private boolean favorite;
    private final boolean removed;

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0026, code lost:
        if (r2.removed == r3.removed) goto L_0x002b;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x002b
            boolean r0 = r3 instanceof com.android.systemui.controls.ControlStatus
            if (r0 == 0) goto L_0x0029
            com.android.systemui.controls.ControlStatus r3 = (com.android.systemui.controls.ControlStatus) r3
            android.service.controls.Control r0 = r2.control
            android.service.controls.Control r1 = r3.control
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0029
            android.content.ComponentName r0 = r2.component
            android.content.ComponentName r1 = r3.component
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0029
            boolean r0 = r2.favorite
            boolean r1 = r3.favorite
            if (r0 != r1) goto L_0x0029
            boolean r2 = r2.removed
            boolean r3 = r3.removed
            if (r2 != r3) goto L_0x0029
            goto L_0x002b
        L_0x0029:
            r2 = 0
            return r2
        L_0x002b:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.ControlStatus.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        Control control2 = this.control;
        int i = 0;
        int hashCode = (control2 != null ? control2.hashCode() : 0) * 31;
        ComponentName componentName = this.component;
        if (componentName != null) {
            i = componentName.hashCode();
        }
        int i2 = (hashCode + i) * 31;
        int i3 = this.favorite;
        int i4 = 1;
        if (i3 != 0) {
            i3 = 1;
        }
        int i5 = (i2 + i3) * 31;
        boolean z = this.removed;
        if (!z) {
            i4 = z;
        }
        return i5 + i4;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ControlStatus(control=");
        sb.append(this.control);
        sb.append(", component=");
        sb.append(this.component);
        sb.append(", favorite=");
        sb.append(this.favorite);
        sb.append(", removed=");
        sb.append(this.removed);
        sb.append(")");
        return sb.toString();
    }

    public ControlStatus(Control control2, ComponentName componentName, boolean z, boolean z2) {
        Intrinsics.checkParameterIsNotNull(control2, "control");
        Intrinsics.checkParameterIsNotNull(componentName, "component");
        this.control = control2;
        this.component = componentName;
        this.favorite = z;
        this.removed = z2;
    }

    public final Control getControl() {
        return this.control;
    }

    public final ComponentName getComponent() {
        return this.component;
    }

    public final boolean getFavorite() {
        return this.favorite;
    }

    public /* synthetic */ ControlStatus(Control control2, ComponentName componentName, boolean z, boolean z2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i & 8) != 0) {
            z2 = false;
        }
        this(control2, componentName, z, z2);
    }

    public final boolean getRemoved() {
        return this.removed;
    }
}
