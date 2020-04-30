package com.android.systemui.controls.p004ui;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;
import kotlin.jvm.internal.Intrinsics;

/* renamed from: com.android.systemui.controls.ui.SelectionItem */
/* compiled from: ControlsUiControllerImpl.kt */
final class SelectionItem {
    private final CharSequence appName;
    private final ComponentName componentName;
    private final Drawable icon;
    private final CharSequence structure;

    public static /* synthetic */ SelectionItem copy$default(SelectionItem selectionItem, CharSequence charSequence, CharSequence charSequence2, Drawable drawable, ComponentName componentName2, int i, Object obj) {
        if ((i & 1) != 0) {
            charSequence = selectionItem.appName;
        }
        if ((i & 2) != 0) {
            charSequence2 = selectionItem.structure;
        }
        if ((i & 4) != 0) {
            drawable = selectionItem.icon;
        }
        if ((i & 8) != 0) {
            componentName2 = selectionItem.componentName;
        }
        return selectionItem.copy(charSequence, charSequence2, drawable, componentName2);
    }

    public final SelectionItem copy(CharSequence charSequence, CharSequence charSequence2, Drawable drawable, ComponentName componentName2) {
        Intrinsics.checkParameterIsNotNull(charSequence, "appName");
        Intrinsics.checkParameterIsNotNull(charSequence2, "structure");
        Intrinsics.checkParameterIsNotNull(drawable, "icon");
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        return new SelectionItem(charSequence, charSequence2, drawable, componentName2);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x002e, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.componentName, (java.lang.Object) r3.componentName) != false) goto L_0x0033;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0033
            boolean r0 = r3 instanceof com.android.systemui.controls.p004ui.SelectionItem
            if (r0 == 0) goto L_0x0031
            com.android.systemui.controls.ui.SelectionItem r3 = (com.android.systemui.controls.p004ui.SelectionItem) r3
            java.lang.CharSequence r0 = r2.appName
            java.lang.CharSequence r1 = r3.appName
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0031
            java.lang.CharSequence r0 = r2.structure
            java.lang.CharSequence r1 = r3.structure
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0031
            android.graphics.drawable.Drawable r0 = r2.icon
            android.graphics.drawable.Drawable r1 = r3.icon
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0031
            android.content.ComponentName r2 = r2.componentName
            android.content.ComponentName r3 = r3.componentName
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x0031
            goto L_0x0033
        L_0x0031:
            r2 = 0
            return r2
        L_0x0033:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.controls.p004ui.SelectionItem.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        CharSequence charSequence = this.appName;
        int i = 0;
        int hashCode = (charSequence != null ? charSequence.hashCode() : 0) * 31;
        CharSequence charSequence2 = this.structure;
        int hashCode2 = (hashCode + (charSequence2 != null ? charSequence2.hashCode() : 0)) * 31;
        Drawable drawable = this.icon;
        int hashCode3 = (hashCode2 + (drawable != null ? drawable.hashCode() : 0)) * 31;
        ComponentName componentName2 = this.componentName;
        if (componentName2 != null) {
            i = componentName2.hashCode();
        }
        return hashCode3 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SelectionItem(appName=");
        sb.append(this.appName);
        sb.append(", structure=");
        sb.append(this.structure);
        sb.append(", icon=");
        sb.append(this.icon);
        sb.append(", componentName=");
        sb.append(this.componentName);
        sb.append(")");
        return sb.toString();
    }

    public SelectionItem(CharSequence charSequence, CharSequence charSequence2, Drawable drawable, ComponentName componentName2) {
        Intrinsics.checkParameterIsNotNull(charSequence, "appName");
        Intrinsics.checkParameterIsNotNull(charSequence2, "structure");
        Intrinsics.checkParameterIsNotNull(drawable, "icon");
        Intrinsics.checkParameterIsNotNull(componentName2, "componentName");
        this.appName = charSequence;
        this.structure = charSequence2;
        this.icon = drawable;
        this.componentName = componentName2;
    }

    public final CharSequence getAppName() {
        return this.appName;
    }

    public final CharSequence getStructure() {
        return this.structure;
    }

    public final Drawable getIcon() {
        return this.icon;
    }

    public final ComponentName getComponentName() {
        return this.componentName;
    }

    public final CharSequence getTitle() {
        return this.structure.length() == 0 ? this.appName : this.structure;
    }
}
