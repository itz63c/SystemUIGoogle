package com.android.systemui.controls.controller;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlInfo.kt */
public final class ControlInfo {
    private final String controlId;
    private final CharSequence controlSubtitle;
    private final CharSequence controlTitle;
    private final int deviceType;

    /* compiled from: ControlInfo.kt */
    public static final class Builder {
        public String controlId;
        public CharSequence controlSubtitle;
        public CharSequence controlTitle;
        private int deviceType;

        public final void setControlId(String str) {
            Intrinsics.checkParameterIsNotNull(str, "<set-?>");
            this.controlId = str;
        }

        public final void setControlTitle(CharSequence charSequence) {
            Intrinsics.checkParameterIsNotNull(charSequence, "<set-?>");
            this.controlTitle = charSequence;
        }

        public final void setControlSubtitle(CharSequence charSequence) {
            Intrinsics.checkParameterIsNotNull(charSequence, "<set-?>");
            this.controlSubtitle = charSequence;
        }

        public final void setDeviceType(int i) {
            this.deviceType = i;
        }

        public final ControlInfo build() {
            String str = this.controlId;
            if (str != null) {
                CharSequence charSequence = this.controlTitle;
                if (charSequence != null) {
                    CharSequence charSequence2 = this.controlSubtitle;
                    if (charSequence2 != null) {
                        return new ControlInfo(str, charSequence, charSequence2, this.deviceType);
                    }
                    Intrinsics.throwUninitializedPropertyAccessException("controlSubtitle");
                    throw null;
                }
                Intrinsics.throwUninitializedPropertyAccessException("controlTitle");
                throw null;
            }
            Intrinsics.throwUninitializedPropertyAccessException("controlId");
            throw null;
        }
    }

    public static /* synthetic */ ControlInfo copy$default(ControlInfo controlInfo, String str, CharSequence charSequence, CharSequence charSequence2, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            str = controlInfo.controlId;
        }
        if ((i2 & 2) != 0) {
            charSequence = controlInfo.controlTitle;
        }
        if ((i2 & 4) != 0) {
            charSequence2 = controlInfo.controlSubtitle;
        }
        if ((i2 & 8) != 0) {
            i = controlInfo.deviceType;
        }
        return controlInfo.copy(str, charSequence, charSequence2, i);
    }

    public final ControlInfo copy(String str, CharSequence charSequence, CharSequence charSequence2, int i) {
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        Intrinsics.checkParameterIsNotNull(charSequence, "controlTitle");
        Intrinsics.checkParameterIsNotNull(charSequence2, "controlSubtitle");
        return new ControlInfo(str, charSequence, charSequence2, i);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
        if (r2.deviceType == r3.deviceType) goto L_0x002f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x002f
            boolean r0 = r3 instanceof com.android.systemui.controls.controller.ControlInfo
            if (r0 == 0) goto L_0x002d
            com.android.systemui.controls.controller.ControlInfo r3 = (com.android.systemui.controls.controller.ControlInfo) r3
            java.lang.String r0 = r2.controlId
            java.lang.String r1 = r3.controlId
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x002d
            java.lang.CharSequence r0 = r2.controlTitle
            java.lang.CharSequence r1 = r3.controlTitle
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x002d
            java.lang.CharSequence r0 = r2.controlSubtitle
            java.lang.CharSequence r1 = r3.controlSubtitle
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x002d
            int r2 = r2.deviceType
            int r3 = r3.deviceType
            if (r2 != r3) goto L_0x002d
            goto L_0x002f
        L_0x002d:
            r2 = 0
            return r2
        L_0x002f:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.controller.ControlInfo.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.controlId;
        int i = 0;
        int hashCode = (str != null ? str.hashCode() : 0) * 31;
        CharSequence charSequence = this.controlTitle;
        int hashCode2 = (hashCode + (charSequence != null ? charSequence.hashCode() : 0)) * 31;
        CharSequence charSequence2 = this.controlSubtitle;
        if (charSequence2 != null) {
            i = charSequence2.hashCode();
        }
        return ((hashCode2 + i) * 31) + Integer.hashCode(this.deviceType);
    }

    public ControlInfo(String str, CharSequence charSequence, CharSequence charSequence2, int i) {
        Intrinsics.checkParameterIsNotNull(str, "controlId");
        Intrinsics.checkParameterIsNotNull(charSequence, "controlTitle");
        Intrinsics.checkParameterIsNotNull(charSequence2, "controlSubtitle");
        this.controlId = str;
        this.controlTitle = charSequence;
        this.controlSubtitle = charSequence2;
        this.deviceType = i;
    }

    public final String getControlId() {
        return this.controlId;
    }

    public final CharSequence getControlTitle() {
        return this.controlTitle;
    }

    public final CharSequence getControlSubtitle() {
        return this.controlSubtitle;
    }

    public final int getDeviceType() {
        return this.deviceType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(':');
        sb.append(this.controlId);
        sb.append(':');
        sb.append(this.controlTitle);
        sb.append(':');
        sb.append(this.deviceType);
        return sb.toString();
    }
}
