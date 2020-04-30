package com.android.systemui.controls.management;

import kotlin.jvm.internal.Intrinsics;

/* compiled from: ControlsFavoritingActivity.kt */
public final class StructureContainer {
    private final ControlsModel model;
    private final CharSequence structureName;

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.model, (java.lang.Object) r3.model) != false) goto L_0x001f;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x001f
            boolean r0 = r3 instanceof com.android.systemui.controls.management.StructureContainer
            if (r0 == 0) goto L_0x001d
            com.android.systemui.controls.management.StructureContainer r3 = (com.android.systemui.controls.management.StructureContainer) r3
            java.lang.CharSequence r0 = r2.structureName
            java.lang.CharSequence r1 = r3.structureName
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x001d
            com.android.systemui.controls.management.ControlsModel r2 = r2.model
            com.android.systemui.controls.management.ControlsModel r3 = r3.model
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.management.StructureContainer.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        CharSequence charSequence = this.structureName;
        int i = 0;
        int hashCode = (charSequence != null ? charSequence.hashCode() : 0) * 31;
        ControlsModel controlsModel = this.model;
        if (controlsModel != null) {
            i = controlsModel.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StructureContainer(structureName=");
        sb.append(this.structureName);
        sb.append(", model=");
        sb.append(this.model);
        sb.append(")");
        return sb.toString();
    }

    public StructureContainer(CharSequence charSequence, ControlsModel controlsModel) {
        Intrinsics.checkParameterIsNotNull(charSequence, "structureName");
        Intrinsics.checkParameterIsNotNull(controlsModel, "model");
        this.structureName = charSequence;
        this.model = controlsModel;
    }

    public final ControlsModel getModel() {
        return this.model;
    }

    public final CharSequence getStructureName() {
        return this.structureName;
    }
}
