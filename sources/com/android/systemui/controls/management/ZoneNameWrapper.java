package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsModel.kt */
public final class ZoneNameWrapper extends ElementWrapper {
    private final CharSequence zoneName;

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0010, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r1.zoneName, (java.lang.Object) ((com.android.systemui.controls.management.ZoneNameWrapper) r2).zoneName) != false) goto L_0x0015;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r2) {
        /*
            r1 = this;
            if (r1 == r2) goto L_0x0015
            boolean r0 = r2 instanceof com.android.systemui.controls.management.ZoneNameWrapper
            if (r0 == 0) goto L_0x0013
            com.android.systemui.controls.management.ZoneNameWrapper r2 = (com.android.systemui.controls.management.ZoneNameWrapper) r2
            java.lang.CharSequence r1 = r1.zoneName
            java.lang.CharSequence r2 = r2.zoneName
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.management.ZoneNameWrapper.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        CharSequence charSequence = this.zoneName;
        if (charSequence != null) {
            return charSequence.hashCode();
        }
        return 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ZoneNameWrapper(zoneName=");
        sb.append(this.zoneName);
        sb.append(")");
        return sb.toString();
    }

    public ZoneNameWrapper(CharSequence charSequence) {
        Intrinsics.checkParameterIsNotNull(charSequence, "zoneName");
        super(null);
        this.zoneName = charSequence;
    }

    public final CharSequence getZoneName() {
        return this.zoneName;
    }
}
