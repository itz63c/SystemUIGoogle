package com.android.systemui.controls.p004ui;

/* renamed from: com.android.systemui.controls.ui.IconState */
/* compiled from: RenderInfo.kt */
public final class IconState {
    private final int disabledResourceId;
    private final int enabledResourceId;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0012, code lost:
        if (r2.enabledResourceId == r3.enabledResourceId) goto L_0x0017;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0017
            boolean r0 = r3 instanceof com.android.systemui.controls.p004ui.IconState
            if (r0 == 0) goto L_0x0015
            com.android.systemui.controls.ui.IconState r3 = (com.android.systemui.controls.p004ui.IconState) r3
            int r0 = r2.disabledResourceId
            int r1 = r3.disabledResourceId
            if (r0 != r1) goto L_0x0015
            int r2 = r2.enabledResourceId
            int r3 = r3.enabledResourceId
            if (r2 != r3) goto L_0x0015
            goto L_0x0017
        L_0x0015:
            r2 = 0
            return r2
        L_0x0017:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.IconState.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        return (Integer.hashCode(this.disabledResourceId) * 31) + Integer.hashCode(this.enabledResourceId);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IconState(disabledResourceId=");
        sb.append(this.disabledResourceId);
        sb.append(", enabledResourceId=");
        sb.append(this.enabledResourceId);
        sb.append(")");
        return sb.toString();
    }

    public IconState(int i, int i2) {
        this.disabledResourceId = i;
        this.enabledResourceId = i2;
    }

    public final int get(boolean z) {
        if (z) {
            return this.enabledResourceId;
        }
        return this.disabledResourceId;
    }
}
