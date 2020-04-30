package com.android.systemui.statusbar.notification.people;

import android.graphics.drawable.Drawable;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: PeopleHub.kt */
public final class PersonModel {
    private final Drawable avatar;
    private final Runnable clickRunnable;
    private final String key;
    private final CharSequence name;
    private final int userId;

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0034, code lost:
        if (kotlin.jvm.internal.Intrinsics.areEqual((java.lang.Object) r2.clickRunnable, (java.lang.Object) r3.clickRunnable) != false) goto L_0x0039;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r3) {
        /*
            r2 = this;
            if (r2 == r3) goto L_0x0039
            boolean r0 = r3 instanceof com.android.systemui.statusbar.notification.people.PersonModel
            if (r0 == 0) goto L_0x0037
            com.android.systemui.statusbar.notification.people.PersonModel r3 = (com.android.systemui.statusbar.notification.people.PersonModel) r3
            java.lang.String r0 = r2.key
            java.lang.String r1 = r3.key
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0037
            int r0 = r2.userId
            int r1 = r3.userId
            if (r0 != r1) goto L_0x0037
            java.lang.CharSequence r0 = r2.name
            java.lang.CharSequence r1 = r3.name
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0037
            android.graphics.drawable.Drawable r0 = r2.avatar
            android.graphics.drawable.Drawable r1 = r3.avatar
            boolean r0 = kotlin.jvm.internal.Intrinsics.areEqual(r0, r1)
            if (r0 == 0) goto L_0x0037
            java.lang.Runnable r2 = r2.clickRunnable
            java.lang.Runnable r3 = r3.clickRunnable
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r2, r3)
            if (r2 == 0) goto L_0x0037
            goto L_0x0039
        L_0x0037:
            r2 = 0
            return r2
        L_0x0039:
            r2 = 1
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.systemui.statusbar.notification.people.PersonModel.equals(java.lang.Object):boolean");
    }

    public int hashCode() {
        String str = this.key;
        int i = 0;
        int hashCode = (((str != null ? str.hashCode() : 0) * 31) + Integer.hashCode(this.userId)) * 31;
        CharSequence charSequence = this.name;
        int hashCode2 = (hashCode + (charSequence != null ? charSequence.hashCode() : 0)) * 31;
        Drawable drawable = this.avatar;
        int hashCode3 = (hashCode2 + (drawable != null ? drawable.hashCode() : 0)) * 31;
        Runnable runnable = this.clickRunnable;
        if (runnable != null) {
            i = runnable.hashCode();
        }
        return hashCode3 + i;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersonModel(key=");
        sb.append(this.key);
        sb.append(", userId=");
        sb.append(this.userId);
        sb.append(", name=");
        sb.append(this.name);
        sb.append(", avatar=");
        sb.append(this.avatar);
        sb.append(", clickRunnable=");
        sb.append(this.clickRunnable);
        sb.append(")");
        return sb.toString();
    }

    public PersonModel(String str, int i, CharSequence charSequence, Drawable drawable, Runnable runnable) {
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.checkParameterIsNotNull(charSequence, "name");
        Intrinsics.checkParameterIsNotNull(drawable, "avatar");
        Intrinsics.checkParameterIsNotNull(runnable, "clickRunnable");
        this.key = str;
        this.userId = i;
        this.name = charSequence;
        this.avatar = drawable;
        this.clickRunnable = runnable;
    }

    public final String getKey() {
        return this.key;
    }

    public final int getUserId() {
        return this.userId;
    }

    public final CharSequence getName() {
        return this.name;
    }

    public final Drawable getAvatar() {
        return this.avatar;
    }

    public final Runnable getClickRunnable() {
        return this.clickRunnable;
    }
}
