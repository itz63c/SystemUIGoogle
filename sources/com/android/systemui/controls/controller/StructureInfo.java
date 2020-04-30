package com.android.systemui.controls.controller;

import android.content.ComponentName;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: StructureInfo.kt */
public final class StructureInfo {
    private final ComponentName componentName;
    private final List<ControlInfo> controls;
    private final CharSequence structure;

    /* JADX WARNING: Incorrect type for immutable var: ssa=java.util.List, code=java.util.List<com.android.systemui.controls.controller.ControlInfo>, for r3v0, types: [java.util.List] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static /* synthetic */ com.android.systemui.controls.controller.StructureInfo copy$default(com.android.systemui.controls.controller.StructureInfo r0, android.content.ComponentName r1, java.lang.CharSequence r2, java.util.List<com.android.systemui.controls.controller.ControlInfo> r3, int r4, java.lang.Object r5) {
        /*
            r5 = r4 & 1
            if (r5 == 0) goto L_0x0006
            android.content.ComponentName r1 = r0.componentName
        L_0x0006:
            r5 = r4 & 2
            if (r5 == 0) goto L_0x000c
            java.lang.CharSequence r2 = r0.structure
        L_0x000c:
            r4 = r4 & 4
            if (r4 == 0) goto L_0x0012
            java.util.List<com.android.systemui.controls.controller.ControlInfo> r3 = r0.controls
        L_0x0012:
            com.android.systemui.controls.controller.StructureInfo r0 = r0.copy(r1, r2, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.controller.StructureInfo.copy$default(com.android.systemui.controls.controller.StructureInfo, android.content.ComponentName, java.lang.CharSequence, java.util.List, int, java.lang.Object):com.android.systemui.controls.controller.StructureInfo");
    }

    public final StructureInfo copy(ComponentName componentName2, CharSequence charSequence, List<ControlInfo> list) {
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        Intrinsics.checkParameterIsNotNull(charSequence, "structure");
        Intrinsics.checkParameterIsNotNull(list, "controls");
        return new StructureInfo(componentName2, charSequence, list);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0024, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.controls, (java.lang.Object) r3.controls) != false) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0029
            boolean r0 = r3 instanceof com.android.systemui.controls.controller.StructureInfo
            if (r0 == 0) goto L_0x0027
            com.android.systemui.controls.controller.StructureInfo r3 = (com.android.systemui.controls.controller.StructureInfo) r3
            android.content.ComponentName r0 = r2.componentName
            android.content.ComponentName r1 = r3.componentName
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0027
            java.lang.CharSequence r0 = r2.structure
            java.lang.CharSequence r1 = r3.structure
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0027
            java.util.List<com.android.systemui.controls.controller.ControlInfo> r2 = r2.controls
            java.util.List<com.android.systemui.controls.controller.ControlInfo> r3 = r3.controls
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.controller.StructureInfo.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        ComponentName componentName2 = this.componentName;
        int i = 0;
        int hashCode = (componentName2 != null ? componentName2.hashCode() : 0) * 31;
        CharSequence charSequence = this.structure;
        int hashCode2 = (hashCode + (charSequence != null ? charSequence.hashCode() : 0)) * 31;
        List<ControlInfo> list = this.controls;
        if (list != null) {
            i = list.hashCode();
        }
        return hashCode2 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StructureInfo(componentName=");
        sb.append(this.componentName);
        sb.append(", structure=");
        sb.append(this.structure);
        sb.append(", controls=");
        sb.append(this.controls);
        sb.append(")");
        return sb.toString();
    }

    public StructureInfo(ComponentName componentName2, CharSequence charSequence, List<ControlInfo> list) {
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        Intrinsics.checkParameterIsNotNull(charSequence, "structure");
        Intrinsics.checkParameterIsNotNull(list, "controls");
        this.componentName = componentName2;
        this.structure = charSequence;
        this.controls = list;
    }

    public final ComponentName getComponentName() {
        return this.componentName;
    }

    public final CharSequence getStructure() {
        return this.structure;
    }

    public final List<ControlInfo> getControls() {
        return this.controls;
    }
}
