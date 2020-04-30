package com.android.systemui.statusbar.notification.people;

import android.graphics.drawable.Drawable;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHub.kt */
public final class PersonViewModel {
    private final Drawable icon;
    private final CharSequence name;
    private final Function0<Unit> onClick;

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0024, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.onClick, (java.lang.Object) r3.onClick) != false) goto L_0x0029;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0029
            boolean r0 = r3 instanceof com.android.systemui.statusbar.notification.people.PersonViewModel
            if (r0 == 0) goto L_0x0027
            com.android.systemui.statusbar.notification.people.PersonViewModel r3 = (com.android.systemui.statusbar.notification.people.PersonViewModel) r3
            java.lang.CharSequence r0 = r2.name
            java.lang.CharSequence r1 = r3.name
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0027
            android.graphics.drawable.Drawable r0 = r2.icon
            android.graphics.drawable.Drawable r1 = r3.icon
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0027
            kotlin.jvm.functions.Function0<kotlin.Unit> r2 = r2.onClick
            kotlin.jvm.functions.Function0<kotlin.Unit> r3 = r3.onClick
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.people.PersonViewModel.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        CharSequence charSequence = this.name;
        int i = 0;
        int hashCode = (charSequence != null ? charSequence.hashCode() : 0) * 31;
        Drawable drawable = this.icon;
        int hashCode2 = (hashCode + (drawable != null ? drawable.hashCode() : 0)) * 31;
        Function0<Unit> function0 = this.onClick;
        if (function0 != null) {
            i = function0.hashCode();
        }
        return hashCode2 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonViewModel(name=");
        sb.append(this.name);
        sb.append(", icon=");
        sb.append(this.icon);
        sb.append(", onClick=");
        sb.append(this.onClick);
        sb.append(")");
        return sb.toString();
    }

    public PersonViewModel(CharSequence charSequence, Drawable drawable, Function0<Unit> function0) {
        Intrinsics.checkParameterIsNotNull(charSequence, "name");
        Intrinsics.checkParameterIsNotNull(drawable, "icon");
        Intrinsics.checkParameterIsNotNull(function0, "onClick");
        this.name = charSequence;
        this.icon = drawable;
        this.onClick = function0;
    }

    public final Drawable getIcon() {
        return this.icon;
    }

    public final Function0<Unit> getOnClick() {
        return this.onClick;
    }
}
