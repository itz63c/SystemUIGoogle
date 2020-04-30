package com.android.systemui.p007qs.carrier;

/* renamed from: com.android.systemui.qs.carrier.CellSignalState */
/* compiled from: CellSignalState.kt */
public final class CellSignalState {
    public final String contentDescription;
    public final int mobileSignalIconId;
    public final boolean roaming;
    public final String typeContentDescription;
    public final boolean visible;

    public CellSignalState() {
        this(false, 0, null, null, false, 31, null);
    }

    public static /* synthetic */ CellSignalState copy$default(CellSignalState cellSignalState, boolean z, int i, String str, String str2, boolean z2, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            z = cellSignalState.visible;
        }
        if ((i2 & 2) != 0) {
            i = cellSignalState.mobileSignalIconId;
        }
        int i3 = i;
        if ((i2 & 4) != 0) {
            str = cellSignalState.contentDescription;
        }
        String str3 = str;
        if ((i2 & 8) != 0) {
            str2 = cellSignalState.typeContentDescription;
        }
        String str4 = str2;
        if ((i2 & 16) != 0) {
            z2 = cellSignalState.roaming;
        }
        return cellSignalState.copy(z, i3, str3, str4, z2);
    }

    public final CellSignalState copy(boolean z, int i, String str, String str2, boolean z2) {
        CellSignalState cellSignalState = new CellSignalState(z, i, str, str2, z2);
        return cellSignalState;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002c, code lost:
        if (r2.roaming == r3.roaming) goto L_0x0031;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0031
            boolean r0 = r3 instanceof com.android.systemui.p007qs.carrier.CellSignalState
            if (r0 == 0) goto L_0x002f
            com.android.systemui.qs.carrier.CellSignalState r3 = (com.android.systemui.p007qs.carrier.CellSignalState) r3
            boolean r0 = r2.visible
            boolean r1 = r3.visible
            if (r0 != r1) goto L_0x002f
            int r0 = r2.mobileSignalIconId
            int r1 = r3.mobileSignalIconId
            if (r0 != r1) goto L_0x002f
            java.lang.String r0 = r2.contentDescription
            java.lang.String r1 = r3.contentDescription
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x002f
            java.lang.String r0 = r2.typeContentDescription
            java.lang.String r1 = r3.typeContentDescription
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x002f
            boolean r2 = r2.roaming
            boolean r3 = r3.roaming
            if (r2 != r3) goto L_0x002f
            goto L_0x0031
        L_0x002f:
            r2 = 0
            return r2
        L_0x0031:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.p007qs.carrier.CellSignalState.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        boolean z = this.visible;
        int i = 1;
        if (z) {
            z = true;
        }
        int hashCode = (((z ? 1 : 0) * true) + Integer.hashCode(this.mobileSignalIconId)) * 31;
        String str = this.contentDescription;
        int i2 = 0;
        int hashCode2 = (hashCode + (str != null ? str.hashCode() : 0)) * 31;
        String str2 = this.typeContentDescription;
        if (str2 != null) {
            i2 = str2.hashCode();
        }
        int i3 = (hashCode2 + i2) * 31;
        boolean z2 = this.roaming;
        if (!z2) {
            i = z2;
        }
        return i3 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CellSignalState(visible=");
        sb.append(this.visible);
        sb.append(", mobileSignalIconId=");
        sb.append(this.mobileSignalIconId);
        sb.append(", contentDescription=");
        sb.append(this.contentDescription);
        sb.append(", typeContentDescription=");
        sb.append(this.typeContentDescription);
        sb.append(", roaming=");
        sb.append(this.roaming);
        sb.append(")");
        return sb.toString();
    }

    public CellSignalState(boolean z, int i, String str, String str2, boolean z2) {
        this.visible = z;
        this.mobileSignalIconId = i;
        this.contentDescription = str;
        this.typeContentDescription = str2;
        this.roaming = z2;
    }

    public /* synthetic */ CellSignalState(boolean z, int i, String str, String str2, boolean z2, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((i2 & 1) != 0) {
            z = false;
        }
        if ((i2 & 2) != 0) {
            i = 0;
        }
        if ((i2 & 4) != 0) {
            str = null;
        }
        if ((i2 & 8) != 0) {
            str2 = null;
        }
        if ((i2 & 16) != 0) {
            z2 = false;
        }
        this(z, i, str, str2, z2);
    }

    public final CellSignalState changeVisibility(boolean z) {
        if (this.visible == z) {
            return this;
        }
        return copy$default(this, z, 0, null, null, false, 30, null);
    }
}
