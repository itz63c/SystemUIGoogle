package com.android.systemui.controls.p004ui;

import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.DefaultBehavior */
/* compiled from: DefaultBehavior.kt */
public final class DefaultBehavior implements Behavior {
    public ControlViewHolder cvh;

    public void initialize(ControlViewHolder controlViewHolder) {
        Intrinsics.checkParameterIsNotNull(controlViewHolder, "cvh");
        this.cvh = controlViewHolder;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:5:0x001a, code lost:
        if (r4 != null) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void bind(com.android.systemui.controls.p004ui.ControlWithState r4) {
        /*
            r3 = this;
            java.lang.String r0 = "cws"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r4, r0)
            com.android.systemui.controls.ui.ControlViewHolder r0 = r3.cvh
            java.lang.String r1 = "cvh"
            r2 = 0
            if (r0 == 0) goto L_0x0030
            android.widget.TextView r0 = r0.getStatus()
            android.service.controls.Control r4 = r4.getControl()
            if (r4 == 0) goto L_0x001d
            java.lang.CharSequence r4 = r4.getStatusText()
            if (r4 == 0) goto L_0x001d
            goto L_0x001f
        L_0x001d:
            java.lang.String r4 = ""
        L_0x001f:
            r0.setText(r4)
            com.android.systemui.controls.ui.ControlViewHolder r3 = r3.cvh
            if (r3 == 0) goto L_0x002c
            r4 = 2
            r0 = 0
            com.android.systemui.controls.p004ui.ControlViewHolder.m29x1a61c355(r3, r0, r0, r4, r2)
            return
        L_0x002c:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            throw r2
        L_0x0030:
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r1)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.DefaultBehavior.bind(com.android.systemui.controls.ui.ControlWithState):void");
    }
}
